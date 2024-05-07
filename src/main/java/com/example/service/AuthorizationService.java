package com.example.service;

import com.example.constant.UserConstants;
import com.example.entity.AdminEntity;
import com.example.entity.RestUser;
import com.example.repository.AdminRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationService implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(final String userData) throws UsernameNotFoundException {
        log.info("Inside Class: AuthorizationService , Method: loadUserByUsername ");
        final JSONParser jsonParser = new JSONParser();
        // Integer userId1 = null;
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(userData);
            //String userID= String.valueOf(Integer.parseInt(String.valueOf(jsonObject.get(UserConstants.USER_NAME_ATTRIBUTE_USERID))));
            String userName = String.valueOf(jsonObject.get(UserConstants.USER_NAME_ATTRIBUTE_NAME));
            String password = String.valueOf(jsonObject.get(UserConstants.USER_PASSWORD_ATTRIBUTE_NAME));
            String email = String.valueOf(jsonObject.get(UserConstants.USER_ATTRIBUTE_EMAIL));
            log.info("Requested receive by userName{}", userName);

            if (email != null && !email.trim().equals("") ) {
                AdminEntity userInfo = getUserInfo(email);
                String userEmail = userInfo.getEmail();
//                String newPassword = userInfo.getPassword();

                List<GrantedAuthority> grandAuthorities = getGrantedAuthorities(userInfo);
                RestUser restUser = new RestUser(email, userInfo.getPassword(), grandAuthorities);

                restUser.setEmail(email);
                restUser.setPassword(password);
                restUser.setUsername(userInfo.getUserName());

                try {
                    if (userEmail.equals(email)) {
                        return restUser;
                    }
                } catch (NullPointerException e) {
                    System.out.println(UserConstants.ENTER_A_VALID_USER_NAME);
                }
            } else {
                System.out.println(UserConstants.USER_NOT_FOUND);
            }

        } catch (ParseException | JsonProcessingException | InterruptedException e) {
            log.error("Exception in parsing user data", e);
            throw new UsernameNotFoundException(MessageFormat.format("UserInfo not found with userId: {} Message:{}", e.getMessage()));
        }
        return null;
    }

    private List<GrantedAuthority> getGrantedAuthorities(AdminEntity userInfo) {
        log.info("Inside Class: AuthorizationService , Method: getGrantedAuthorities ");
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

//        if (userInfo.getRole() != null ) {
//            grantedAuthorities.add(new SimpleGrantedAuthority(userInfo.getRole().getRoleName())); // changes
//
//        }
//        grantedAuthorities.add(new SimpleGrantedAuthority(Integer.toString(userInfo.getOrganizationId())));
        return grantedAuthorities;
//        grantedAuthorities.add(new SimpleGrantedAuthority(userInfo.toString()));

    }

    private AdminEntity getUserInfo(String userEmail) throws JsonProcessingException, InterruptedException {
        log.info("Inside Class: AuthorizationService , Method: getUserInfo ");
        if (userEmail != null) {
            return getUserFromDB(userEmail);
        }

        return  null ;
    }

    private AdminEntity getUserFromDB(String email) {
        log.info("Inside Class: AuthorizationService , Method: getUserFromDB ");
        AdminEntity user = new AdminEntity();
        AdminEntity userDetailEntity = adminRepository.findByEmail(email);
        if (userDetailEntity != null) {
            user.setUserName(userDetailEntity.getUserName());
            user.setEmail(userDetailEntity.getEmail());
            user.setPassword(userDetailEntity.getPassword());
        }
        return user;
    }
}