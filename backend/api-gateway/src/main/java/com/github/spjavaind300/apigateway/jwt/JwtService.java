package com.github.spjavaind300.apigateway.jwt;

import java.time.Instant;

public interface JwtService {

    boolean isValidToken(String token);

    long getUserId(String token);

    Role getRole(String token);

    Instant getIssuedAt(String token);
}
