package com.github.spjavaind300.profileservice.service;

import com.github.spjavaind300.profileservice.dto.JwtUserInfo;

public interface JwtService {
    JwtUserInfo parseToken(String token);
}

