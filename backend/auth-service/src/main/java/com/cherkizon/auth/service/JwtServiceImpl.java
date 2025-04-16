package com.cherkizon.auth.service;

import com.cherkizon.auth.dto.request.response.JwtResponse;
import com.cherkizon.auth.entity.User;
import com.cherkizon.auth.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Override
    public JwtResponse generateTokens(User user) {
        return new JwtResponse(
                generateToken(new HashMap<>(), user, accessExpiration),
                generateToken(new HashMap<>(), user, refreshExpiration)
        );
    }

    @Override
    public JwtResponse refreshToken(String refreshToken) {
        if (!isTokenValid(refreshToken)) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        String username = extractUsername(refreshToken);
        User user = (User) userDetailsService.loadUserByUsername(username);

        return generateTokens(user);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Дополнительные методы валидации токена
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
    }

    public String extractUsername(String token) {
        // ... извлечение username из токена
    }
}