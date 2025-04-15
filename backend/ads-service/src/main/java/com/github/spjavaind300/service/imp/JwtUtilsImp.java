package com.github.spjavaind300.service.imp;

import com.github.spjavaind300.model.dto.Role;
import com.github.spjavaind300.model.dto.UserContext;
import com.github.spjavaind300.service.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class JwtUtilsImp implements JwtUtils {

    @Override
    public UserContext getUserContext(HttpServletRequest request) {
        return new UserContext(1, Role.USER);
    }
}
