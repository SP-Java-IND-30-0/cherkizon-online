package com.cherkizon.auth.service;

import com.cherkizon.auth.dto.request.LoginRequest;
import com.cherkizon.auth.dto.request.RegisterRequest;
import com.cherkizon.auth.dto.request.response.JwtResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Override
    public void register(RegisterRequest request) {
        // TODO: Реализовать логику
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        // TODO: Реализовать логику
        return null;
    }

}