package com.github.spjavaind300.apigateway.dto;

import java.time.Instant;

public record TokenDto(
        String accessToken,
        String refreshToken,
        long userId,
        Instant expiresAt
) {
}
