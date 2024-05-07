package com.example.exceptionhandler;


import com.example.constant.UserConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
@Component
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private final ObjectMapper objectMapper;

    @Autowired
    public RestExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public void handleException
            (final HttpServletResponse response, String message, HttpStatus status) throws IOException {
        log.info("Inside Class: RestExceptionHandler , Method: handleException ");
        final Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put(UserConstants.MESSAGE, message);
        errorDetails.put(UserConstants.ERROR, status.value());

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getWriter(), errorDetails);
    }


}