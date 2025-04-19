package com.cherkizon.auth.service;

import com.cherkizon.auth.dto.request.LoginRequest;
import com.cherkizon.auth.dto.request.RegisterRequest;
import com.cherkizon.auth.dto.response.JwtResponse;

public interface AuthService {
    void register(RegisterRequest request);

    JwtResponse login(LoginRequest request);

    JwtResponse refreshToken(String refreshToken);

}


