package com.example.controller;


import com.example.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authenticate")
@CrossOrigin
@Slf4j
@RequiredArgsConstructor
public class SecurityController {
    @Autowired
    private TokenService tokenService;
    @PostMapping
    public JSONObject generateToken(@RequestBody String payload) {
        log.info("Inside Class: SecurityController , Method: generateToken()");
        return tokenService.createJwtSignedHMAC(payload);
    }
}