package com.github.spjavaind300.profileservice.service.impl;

import com.github.spjavaind300.profileservice.client.AuthClient;
import com.github.spjavaind300.profileservice.dto.JwtUserInfo;
import com.github.spjavaind300.profileservice.dto.UpdatePasswordDTO;
import com.github.spjavaind300.profileservice.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PasswordServiceImpl implements PasswordService {

    private final JwtServiceImpl jwtService;
    private final AuthClient authClient;

    @Autowired
    public PasswordServiceImpl(JwtServiceImpl jwtService, AuthClient authClient) {
        this.jwtService = jwtService;
        this.authClient = authClient;
    }

    @Override
    public void changePassword(String token, UpdatePasswordDTO passwordDTO) {
        JwtUserInfo jwtUser = jwtService.parseToken(token);
        authClient.changePassword(jwtUser.getUserId(), passwordDTO);
    }
}
