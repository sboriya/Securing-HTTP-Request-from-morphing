package com.example.service;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class HashingService {

    private static final String SALT = "its_my_secure_salt_@123";

    public static String calculateSHA256Hash(String data) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(SALT.getBytes(), "HmacSHA256");
            sha256Hmac.init(secretKey);

            byte[] hashBytes = sha256Hmac.doFinal((data).getBytes());
            String hash = Base64.getEncoder().encodeToString(hashBytes);

            return hash;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }
}
