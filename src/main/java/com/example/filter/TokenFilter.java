package com.example.filter;


import com.example.constant.SecurityConstants;
import com.example.constant.UserConstants;
import com.example.exceptionhandler.RestExceptionHandler;
import com.example.service.AuthorizationService;
import com.example.service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class TokenFilter  {

    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private RestExceptionHandler restExceptionHandler;
    @Autowired
    private TokenService tokenService;


    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, jakarta.servlet.FilterChain filterChain) throws jakarta.servlet.ServletException, IOException {
        log.info("Inside Class: TokenFilter , Method: doFilterInternal() ");
        final String requestTokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        String userName;
        String password;
        String userID;

        String jwtToken;
        String tokenData;
        JSONObject jsonObject;


        if (requestTokenHeader !=
                null)
            if (requestTokenHeader.startsWith(SecurityConstants.BEARER_HEADER_NAME)) {
                jwtToken = requestTokenHeader.substring(SecurityConstants.BEARER_HEADER_NAME.length());
                try {
                    tokenData = tokenService.getSubjectFromToken(jwtToken);
                    final JSONParser jsonParser = new JSONParser();
                    log.info("tokenData  {}", tokenData);
                    jsonObject = (JSONObject) jsonParser.parse(tokenData);


                    userID= String.valueOf(Integer.parseInt(String.valueOf(jsonObject.get(UserConstants.USER_NAME_ATTRIBUTE_USERID))));
                    userName = String.valueOf(jsonObject.get(UserConstants.USER_NAME_ATTRIBUTE_NAME));
                    password = String.valueOf(jsonObject.get(UserConstants.USER_PASSWORD_ATTRIBUTE_NAME));

                    request.setAttribute(UserConstants.USER_NAME_ATTRIBUTE_NAME, userName);
                    request.setAttribute(UserConstants.USER_PASSWORD_ATTRIBUTE_NAME, password);
                    request.setAttribute(UserConstants.USER_NAME_ATTRIBUTE_USERID,userID);

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

                } catch (IllegalArgumentException e) {
                    restExceptionHandler.handleException(response, SecurityConstants.MSG_UNABLE_TO_GET_JWT_TOKEN, HttpStatus.FORBIDDEN);
                    log.error(SecurityConstants.MSG_UNABLE_TO_GET_JWT_TOKEN, e);
                } catch (ExpiredJwtException e) {
                    restExceptionHandler.handleException(response, SecurityConstants.MSG_JWT_TOKEN_HAS_EXPIRED, HttpStatus.FORBIDDEN);
                    log.error(SecurityConstants.MSG_JWT_TOKEN_HAS_EXPIRED, e);
                } catch (ParseException e) {
                    restExceptionHandler.handleException
                            (response, SecurityConstants.MSG_JWT_TOKEN_CANNOT_BE_PARSED, HttpStatus.FORBIDDEN);
                    log.error(SecurityConstants.MSG_JWT_TOKEN_CANNOT_BE_PARSED, e);
                }
            }
        filterChain.doFilter(request, response);
    }
}