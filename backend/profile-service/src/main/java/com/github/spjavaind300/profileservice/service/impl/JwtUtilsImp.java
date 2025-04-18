package com.github.spjavaind300.profileservice.service.impl;


import com.github.spjavaind300.profileservice.dto.Role;
import com.github.spjavaind300.profileservice.exception.InvalidJwtException;
import com.github.spjavaind300.profileservice.security.CustomUserDetails;
import com.github.spjavaind300.profileservice.service.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.function.Function;

@Service
public class JwtUtilsImp implements JwtUtils {

    private final String JwtSecret;

    public JwtUtilsImpl(
            @Qualifier("jwtSecret") String jwtSecret
    ) {
        this.JwtSecret = jwtSecret;
    }

    //TODO temporary method
    @Override
    public String generateToken(long userId, String role) {
        long jwtExpiation = (long) 1000 * 60 * 60;
        return buildToken(new CustomUserDetails(userId, Role.valueOf(role)), jwtExpiation);
    }

    @Override
    public long getUserId(String token) {
        try {
            return Long.parseLong(extractClaim(token, Claims::getSubject));
        } catch (Exception e) {
            throw new InvalidJwtException(e);
        }
    }

    @Override
    public Role getRole(String token) {
        try {
            return Role.valueOf(extractClaim(token, claims -> claims.get("role", String.class)));
        } catch (Exception e) {
            throw new InvalidJwtException(e);
        }
    }

    @Override
    public boolean isValidToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String buildToken(CustomUserDetails userContext, long jwtExpiration) {
        return Jwts.builder()
                .subject(Long.toString(userContext.userId()))
                .claim("role", userContext.role())
                .expiration(Timestamp.from(Instant.now().plusSeconds(jwtExpiration)))
                .signWith(getSigningKey())
                .compact();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }
}
