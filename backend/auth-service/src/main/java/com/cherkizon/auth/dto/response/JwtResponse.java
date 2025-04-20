package com.cherkizon.auth.dto.response;


import java.time.Instant;

public record JwtResponse(
        String accessToken,
        String refreshToken,
        Long userId,
        Instant expiresAt) {
}
