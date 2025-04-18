package com.github.spjavaind300.profileservice.service;

import com.github.spjavaind300.profileservice.dto.Role;

public interface JwtUtils {

    boolean isValidToken(String token);

    long getUserId(String token);

    Role getRole(String token);

    String generateToken(long userId, String role);

}
