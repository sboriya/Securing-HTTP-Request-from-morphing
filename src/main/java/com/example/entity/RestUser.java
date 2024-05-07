package com.example.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.Serial;
import java.util.Collection;

@Getter
@Setter
public class RestUser extends User {

    @Serial
    private static final long serialVersionUID = 5926468583005150707L;

    private String username;
    private String password;
    private String userRequest;
    private String email;

    public RestUser(String email, String password, Collection<? extends GrantedAuthority> authorities) {
        super(email, password, authorities);
    }
}