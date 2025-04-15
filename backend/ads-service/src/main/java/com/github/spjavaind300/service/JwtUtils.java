package com.github.spjavaind300.service;

import com.github.spjavaind300.model.dto.UserContext;
import jakarta.servlet.http.HttpServletRequest;

public interface JwtUtils {

    UserContext getUserContext(HttpServletRequest request);
}
