package com.github.spjavaind300.profileservice.service.impl;

import com.github.spjavaind300.profileservice.dto.JwtUserInfo;
import com.github.spjavaind300.profileservice.service.JwtService;
import org.springframework.stereotype.Service;

@Service
public class JwtServiceImpl implements JwtService {

    @Override
    public JwtUserInfo parseToken(String token) {
        return new JwtUserInfo(1L, "USER");
    }
}
