package com.cherkizon.auth.service;

import com.cherkizon.auth.dto.request.response.JwtResponse;

public interface JwtService {
    JwtResponse refreshToken(String refreshToken);
}
