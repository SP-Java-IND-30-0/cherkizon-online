package com.github.spjavaind300.profileservice.service.impl;

import com.github.spjavaind300.profileservice.client.AuthClient;
import com.github.spjavaind300.profileservice.dto.JwtUserInfo;
import com.github.spjavaind300.profileservice.dto.UpdatePasswordDTO;
import com.github.spjavaind300.profileservice.service.JwtService;
import com.github.spjavaind300.profileservice.service.PasswordService;
import org.springframework.stereotype.Service;

@Service
public class PasswordServiceImpl implements PasswordService {

    private final AuthClient authClient;

    public PasswordServiceImpl(AuthClient authClient) {
        this.authClient = authClient;
    }

    @Override
    public void changePassword(long userId, UpdatePasswordDTO passwordDTO) {
        authClient.changePassword(userId, passwordDTO);
    }
}
