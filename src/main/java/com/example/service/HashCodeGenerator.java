package com.example.service;

import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class HashCodeGenerator {

    public String createHash(byte[] request) {
        String hash = DigestUtils.md5DigestAsHex(request);
        return hash;
    }

}
