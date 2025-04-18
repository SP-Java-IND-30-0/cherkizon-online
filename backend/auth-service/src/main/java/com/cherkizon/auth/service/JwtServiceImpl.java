package com.cherkizon.auth.service;

import com.cherkizon.auth.dto.request.response.JwtResponse;
import com.cherkizon.auth.entity.Token;
import com.cherkizon.auth.entity.User;
import com.cherkizon.auth.exception.InvalidTokenException;
import com.cherkizon.auth.repository.TokenRepository;
import com.cherkizon.auth.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtServiceImpl implements JwtService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Override
    public JwtResponse generateTokens(User user) {
        return new JwtResponse(
                generateToken(Map.of("role", user.getRole().name()), user, accessExpiration),
                generateToken(Map.of("role", user.getRole().name()), user, refreshExpiration)
        );
    }

    @Override
    @Transactional
    public JwtResponse refreshToken(String rawRefreshToken) {
        if (!isTokenValid(rawRefreshToken)) {
            log.error("Invalid refresh token attempt");
            throw new InvalidTokenException("Invalid refresh token");
        }

        Token savedToken = tokenRepository.findAll().stream()
                .filter(token -> passwordEncoder.matches(rawRefreshToken, token.getRefreshToken()))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Refresh token not found or mismatched");
                    return new InvalidTokenException("Refresh token not found or invalid");
                });

        if (savedToken.getExpiresAt().isBefore(Instant.now())) {
            tokenRepository.delete(savedToken);
            log.warn("Refresh token expired");
            throw new InvalidTokenException("Refresh token expired");
        }

        Long userId = extractUserId(rawRefreshToken);
        User tokenUser = savedToken.getUser();

        if (!tokenUser.getId().equals(userId)) {
            log.warn("Token-user mismatch: {}, {}", userId, tokenUser.getUsername());
            tokenRepository.delete(savedToken);
            throw new InvalidTokenException("Token-user mismatch");
        }

        JwtResponse newTokens = generateTokens(tokenUser);

        tokenRepository.delete(savedToken);

        Token newToken = Token.builder()
                .refreshToken(passwordEncoder.encode(newTokens.refreshToken()))
                .user(tokenUser)
                .expiresAt(Instant.now().plusMillis(refreshExpiration))
                .build();
        tokenRepository.save(newToken);

        log.info("Tokens refreshed for user: {}", userId);
        return newTokens;
    }

    private String generateToken(Map<String, Object> claims, User user, long expiration) {
        Instant now = Instant.now();
        return Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(user.getId()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expiration)))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }


    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.warn("Token expired: {}", ex.getMessage());
        } catch (Exception ex) {
            log.warn("Invalid token: {}", ex.getMessage());
        }
        return false;
    }

    public Long extractUserId(String token) {
        return Long.parseLong(extractClaim(token, Claims::getSubject));
    }


    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
