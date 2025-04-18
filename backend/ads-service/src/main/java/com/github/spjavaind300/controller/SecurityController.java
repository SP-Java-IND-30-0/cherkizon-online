package com.github.spjavaind300.controller;

import com.github.spjavaind300.model.dto.Role;
import com.github.spjavaind300.service.JwtUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
                Instant.now().plusSeconds(60*60));
    }

    public static record TokenRequest(
            String username,
            String password
    ) {

    }

    public static record TokenResponse(
            String accessToken,
            String refreshToken,
            Long userId,
            Instant expiresAt

    ) {

    }
}
