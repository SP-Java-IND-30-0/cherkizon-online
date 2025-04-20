package com.github.spjavaind300.commentsservice.security;

import com.github.spjavaind300.commentsservice.dto.Role;
import com.github.spjavaind300.commentsservice.dto.UserContext;

public interface JwtUtils {

    UserContext getUserContext();

    long getUserId(String token);

    Role getRole(String token);

    boolean isValidToken(String token);
}