package com.cherkizon.auth.service;

import com.cherkizon.auth.dto.request.response.JwtResponse;
import com.cherkizon.auth.entity.User;
import com.cherkizon.auth.exception.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.cherkizon.auth.service.loggerService.ServiceLogger.JWT;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private long accessExpiration; // 24 часа

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration; // 7 дней

    /**
     * Генерация новой пары токенов (access + refresh)
     */
    @Override
    public JwtResponse generateTokens(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());

        return new JwtResponse(
                generateToken(claims, user, accessExpiration),
                generateToken(claims, user, refreshExpiration)
        );
    }

    /**
     * Обновление токенов по refresh-токену
     */
    @Override
    public JwtResponse refreshToken(String refreshToken) {
        if (!isTokenValid(refreshToken)) {
            JWT.error("Invalid refresh token attempt");
            throw new InvalidTokenException("Invalid refresh token");
        }

        String username = extractUsername(refreshToken);
        User user = User.builder()
                .username(username)
                .role(User.Role.valueOf(extractClaim(refreshToken, claims -> claims.get("role", String.class))))
                        .build();

        JWT.info("Tokens refreshed for user: {}", username);
        return generateTokens(user);
    }


    private String generateToken(Map<String, Object> claims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Проверка валидности токена
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            JWT.warn("Token expired: {}", ex.getMessage());
        } catch (Exception ex) {
            JWT.warn("Invalid token: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Извлечение username из токена
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Общий метод для извлечения данных из токена
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}