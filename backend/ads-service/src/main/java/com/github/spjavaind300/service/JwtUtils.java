package com.github.spjavaind300.service;

import com.github.spjavaind300.model.dto.Role;
import com.github.spjavaind300.model.dto.UserContext;
import jakarta.servlet.http.HttpServletRequest;

public interface JwtUtils {

    UserContext getUserContext(HttpServletRequest request);

    boolean isValidToken(String token);

    long getUserId(String token);

    Role getRole(String token);
}
