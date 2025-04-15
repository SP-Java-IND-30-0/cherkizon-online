package com.github.spjavaind300.commentsservice.service;

import com.github.spjavaind300.commentsservice.dto.UserContext;
import jakarta.servlet.http.HttpServletRequest;

public interface JwtUtils {

    UserContext getUserContext(HttpServletRequest request);
}