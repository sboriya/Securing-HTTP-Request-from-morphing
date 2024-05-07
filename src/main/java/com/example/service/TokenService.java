package com.example.service;

import com.example.entity.AdminEntity;
import com.example.entity.RestUser;
import com.example.repository.AdminRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.io.Serial;
import java.security.Key;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;
@Service
public class TokenService {
    static  String name;
    @Autowired
    AdminRepository adminRepository;
    @Serial
    private static final long serialVersionUID = -2550185165626007488L;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.secret.token.expiration.minutes}")
    private long tokenExpirationMinutes;
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public String getSubjectFromToken(final String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Boolean validateToken(final String token, UserDetails userDetails) {
        String subject = getSubjectFromToken(token);
        try {
            final JSONParser jsonParser = new JSONParser();
            final org.json.simple.JSONObject jsonObject = (JSONObject) jsonParser.parse(subject);
            String email = (String) jsonObject.get("email");
            return email.equals(((RestUser) userDetails).getEmail()) && !isTokenExpired(token);
        } catch (ParseException e) {
            return false;
        }
    }

    public JSONObject createJwtSignedHMAC(final String authData) {
        try {
            final JSONParser jsonParser = new JSONParser();
            final org.json.simple.JSONObject jsonObject = (JSONObject) jsonParser.parse(authData);
            String email = (String) jsonObject.get("email");
            String passwordVerification = (String) jsonObject.get("password");

            HashAlgorithm hashAlgorithm = new HashAlgorithm();
            String password = hashAlgorithm.encryptThisString(passwordVerification).trim();

            if (StringUtils.isNotBlank(email) && StringUtils.isNotBlank(password)) {
                AdminEntity userInfo = getUserInfo(email);

                String userEmail = userInfo.getEmail();
                String newPassword = userInfo.getPassword().trim();
                try {
                    boolean nameEquals = userEmail.equals(email);
                    boolean passEquals = StringUtils.compare(newPassword, password) == 0;
                    if (nameEquals && passEquals) {
                        final Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS256.getJcaName());
                        JSONObject jsonObjPayload = new JSONObject();
                        jsonObjPayload.put("email", userEmail);
                        jsonObjPayload.put("password",newPassword);
                        Instant now = Instant.now();
                        String token = Jwts.builder().claim("authDate", jsonObjPayload.toString()).setSubject(jsonObjPayload.toString()).setId(UUID.randomUUID().toString()).setIssuedAt(Date.from(now)).setExpiration(Date.from(now.plus(tokenExpirationMinutes, ChronoUnit.MINUTES))).signWith(hmacKey).compact();

                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("token", token);
                        jsonObj.put("role", "admin");
                        jsonObj.put("token_expiry", tokenExpirationMinutes);

                        return jsonObj;
                    } else {
                        throw new UsernameNotFoundException(MessageFormat.format("User with email not found with userName: {0}", email));
                    }
                } catch (Exception e) {
                    throw new UsernameNotFoundException(MessageFormat.format("UserInfo not found with userName: {} Message:{}", name, e.getMessage()));

                }
            } else {
                throw new UsernameNotFoundException(MessageFormat.format("User with email not found with userName: {0}", email));
            }

        } catch (ParseException | JsonProcessingException | InterruptedException e) {
            throw new UsernameNotFoundException(MessageFormat.format("UserInfo not found with userName: {} Message:{}", name, e.getMessage()));
        }
    }

    private AdminEntity getUserFromDB(String email) {
        AdminEntity user = new AdminEntity();
        AdminEntity userDetailEntity = adminRepository.findByEmail(email);
        if (userDetailEntity != null) {
            user.setUserName(userDetailEntity.getUserName());
            user.setPassword(userDetailEntity.getPassword());
            user.setEmail(userDetailEntity.getEmail());
        }
        return user;
    }

    private AdminEntity getUserInfo(String email) throws JsonProcessingException, InterruptedException {
        if (email != null) {
            return getUserFromDB(email);
        }
        return null;
    }

    public void setTokenExpirationMinutes(long tokenExpirationMinutes) {
        this.tokenExpirationMinutes = tokenExpirationMinutes;
    }
}


