package com.github.spjavaind300.profileservice.security;


import com.github.spjavaind300.profileservice.dto.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    public void serviceAuthentication(long id) {
        CustomUserDetails serviceUser = new CustomUserDetails(id, Role.SERVICE);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(serviceUser, null, serviceUser.getAuthorities())
        );
    }
}

