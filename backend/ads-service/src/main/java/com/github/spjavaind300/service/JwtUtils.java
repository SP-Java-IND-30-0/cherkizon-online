package com.github.spjavaind300.service;

import com.github.spjavaind300.model.dto.Role;

public interface JwtUtils {

    boolean isValidToken(String token);

    long getUserId(String token);

    Role getRole(String token);

}
