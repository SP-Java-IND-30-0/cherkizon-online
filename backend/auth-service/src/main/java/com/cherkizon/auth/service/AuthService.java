package com.cherkizon.auth.service;

import com.cherkizon.auth.dto.LoginRequest;
import com.cherkizon.auth.dto.RegisterRequest;
import com.cherkizon.auth.dto.response.JwtResponse;

public interface AuthService {
    Long register(RegisterRequest request);
    JwtResponse login(LoginRequest request);
}