package com.example.config;


import com.example.constant.SecurityConstants;
import com.example.constant.UserConstants;
import com.example.service.AuthorizationService;
import com.example.service.HashingService;
import com.example.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private AuthorizationService authorizationService;
    //final String ALLOWED_APPLICATION_IDENTIFIER = "Rest";

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    private TokenService tokenService;

    @Autowired
    HashingService hashingService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        String XRequestHash = null;
        if (request.getHeader("X-Request-Hash") != null) {
            XRequestHash = request.getHeader("X-Request-Hash");
        }

        String userName;
        String email;
        String password;

        String jwtToken;
        String tokenData;
        JSONObject jsonObject;

        // if (ALLOWED_APPLICATION_IDENTIFIER.equals(applicationIdentifier)) {
        if (requestTokenHeader !=
                null)
            if (requestTokenHeader.startsWith(SecurityConstants.BEARER_HEADER_NAME)) {
                jwtToken = requestTokenHeader.substring(SecurityConstants.BEARER_HEADER_NAME.length());
                try {
                    tokenData = tokenService.getSubjectFromToken(jwtToken);
                    final JSONParser jsonParser = new JSONParser();
                    jsonObject = (JSONObject) jsonParser.parse(tokenData);


                    email = String.valueOf(jsonObject.get(UserConstants.EMAIL));

                    userName = String.valueOf(jsonObject.get(UserConstants.USER_NAME_ATTRIBUTE_NAME));
                    password = String.valueOf(jsonObject.get(UserConstants.USER_PASSWORD_ATTRIBUTE_NAME));
                    request.setAttribute(UserConstants.EMAIL, email);

                    request.setAttribute(UserConstants.USER_NAME_ATTRIBUTE_NAME, userName);
                    request.setAttribute(UserConstants.USER_PASSWORD_ATTRIBUTE_NAME, password);

                    if (tokenData != null && SecurityContextHolder.getContext().
                            getAuthentication() == null) {
                        final UserDetails userDetails = this.authorizationService.loadUserByUsername
                                (jsonObject.toString());
                        if (tokenService.validateToken(jwtToken, userDetails)) {
                            final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails, null, userDetails.getAuthorities());
                            usernamePasswordAuthenticationToken.
                                    setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().
                                    setAuthentication(usernamePasswordAuthenticationToken);
                        }
                    }

                } catch (Exception e) {
                    e.getStackTrace();

                }
            }

        HttpServletRequest customRequest = new CustomHttpServletRequestWrapper(request);
        if ("POST".equals(customRequest.getMethod()) && XRequestHash != null) {

            byte[] requestBody = customRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator())).getBytes();
            String requestBodyString = new String(requestBody, StandardCharsets.UTF_8);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(requestBodyString);
            String calculatedHash1 = hashingService.calculateSHA256Hash(jsonString);
            System.out.println(jsonString);


            if (!calculatedHash1.equals(XRequestHash) && !customRequest.getRequestURI().equals("/authenticate")) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    return;
            }
        }

        filterChain.doFilter(customRequest, response);
    }
}
