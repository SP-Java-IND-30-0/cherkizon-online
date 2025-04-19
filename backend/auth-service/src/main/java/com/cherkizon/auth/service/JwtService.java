package com.cherkizon.auth.service;

import com.cherkizon.auth.dto.response.JwtResponse;
import com.cherkizon.auth.entity.User;

public interface JwtService {
    JwtResponse generateTokens(User user);

    JwtResponse refreshToken(String refreshToken);
}
