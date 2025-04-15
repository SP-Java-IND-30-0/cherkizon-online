package com.github.spjavaind300.commentsservice.service;

import com.github.spjavaind300.commentsservice.dto.Role;
import com.github.spjavaind300.commentsservice.dto.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class JwtUtilsImpl implements JwtUtils {

    @Override
    public UserContext getUserContext(HttpServletRequest request) {
        long authorId = 1L;
        Role role = Role.USER;
        return new UserContext(authorId, role);
    }
}