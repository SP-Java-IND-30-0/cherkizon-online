package com.cherkizon.auth.service;

import com.cherkizon.auth.dto.request.LoginRequest;
import com.cherkizon.auth.dto.request.RegisterRequest;
import com.cherkizon.auth.dto.request.response.JwtResponse;
import com.cherkizon.auth.dto.request.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Override
    public UserResponse register(RegisterRequest request) {
        // TODO: Реализовать логику
        return null;
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        // TODO: Реализовать логику
        return null;
    }

}