package com.cherkizon.auth.service.impl;

import com.cherkizon.auth.dto.response.JwtResponse;
import com.cherkizon.auth.entity.Token;
import com.cherkizon.auth.entity.User;
import com.cherkizon.auth.exception.InvalidTokenException;
import com.cherkizon.auth.repository.TokenRepository;
import com.cherkizon.auth.repository.UserRepository;

import com.cherkizon.auth.service.JwtService;
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
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtServiceImpl implements JwtService {

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
        String accessToken = generateToken(Map.of("role", user.getRole().name()), user, accessExpiration);
        String refreshToken = generateToken(Map.of("role", user.getRole().name()), user, refreshExpiration);

        return new JwtResponse(
                accessToken,
                refreshToken,
                user.getId(),
                Instant.now().plusMillis(accessExpiration)
        );
    }


    @Override
    @Transactional
    public JwtResponse refreshToken(String rawRefreshToken) {
        Claims claims;
        try {
            claims = extractAllClaims(rawRefreshToken);
        } catch (ExpiredJwtException ex) {
            log.warn("Refresh token expired: {}", ex.getMessage());
            throw new InvalidTokenException("Refresh token expired");
        } catch (Exception ex) {
            log.error("Invalid refresh token: {}", ex.getMessage());
            throw new InvalidTokenException("Invalid refresh token");
        }

        Long userId = Long.parseLong(claims.getSubject());

        List<Token> userTokens = tokenRepository.findAllByUserId(userId);

        Token savedToken = userTokens.stream()
                .filter(token -> passwordEncoder.matches(rawRefreshToken, token.getRefreshToken()))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Refresh token not found or mismatched");
                    return new InvalidTokenException("Refresh token not found or invalid");
                });

        User tokenUser = savedToken.getUser();

        if (!tokenUser.getId().equals(userId)) {
            log.warn("Token-user mismatch: {}, {}", userId, tokenUser.getUsername());
            tokenRepository.delete(savedToken);
            throw new InvalidTokenException("Token-user mismatch");
        }

        if (savedToken.getExpiresAt().isBefore(Instant.now())) {
            log.warn("Token in DB expired");
            tokenRepository.delete(savedToken);
            throw new InvalidTokenException("Refresh token expired");
        }

        tokenRepository.delete(savedToken);

        JwtResponse newTokens = generateTokens(tokenUser);

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
       long jwtExpiration = expiration /1000;
        Instant now = Instant.now();
        return Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(user.getId()))
                .issuedAt(Date.from(now))
                .expiration(Timestamp.from(Instant.now().plusSeconds(jwtExpiration)))
                .signWith(getSignInKey())
                .compact();
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
