package com.github.spjavaind300.commentsservice.controller;

import com.github.spjavaind300.commentsservice.dto.Role;
import com.github.spjavaind300.commentsservice.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;


//TODO temporary class
@RestController
@RequestMapping("/internal/security")
@RequiredArgsConstructor
public class SecurityController {

    private final JwtUtils jwtUtils;

    @PostMapping
    public TokenResponse login(@RequestBody TokenRequest request) {
        String accessToken = jwtUtils.generateToken(1L, Role.USER.name());
        return new TokenResponse(
                accessToken,
                accessToken,
                1L,
                Instant.now().plusSeconds(60 * 60));
    }

    public record TokenRequest(
            String username,
            String password
    ) {
    }

    public record TokenResponse(
            String accessToken,
            String refreshToken,
            Long userId,
            Instant expiresAt

    ) {
    }
}