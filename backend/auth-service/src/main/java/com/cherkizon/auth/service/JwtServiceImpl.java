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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static com.cherkizon.auth.service.loggerService.ServiceLogger.JWT;

@RequiredArgsConstructor
@Service
public class JwtServiceImpl implements JwtService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

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
    public JwtResponse refreshToken(String refreshToken) {
        if (!isTokenValid(refreshToken)) {
            JWT.error("Invalid refresh token attempt");
            throw new InvalidTokenException("Invalid refresh token");
        }

        String username = extractUsername(refreshToken);
        String role = extractClaim(refreshToken, claims -> claims.get("role", String.class));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Token savedToken = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found or already rotated"));

        if (savedToken.isExpired() || savedToken.isRevoked()) {
            JWT.warn("Refresh token expired/revoked for user: {}", username);
            throw new InvalidTokenException("Refresh token is not valid anymore");
        }

        JwtResponse newTokens = generateTokens(user);

        Token newToken = Token.builder()
                .refreshToken(newTokens.refreshToken())
                .user(user)
                .expired(false)
                .revoked(false)
                .build();

        tokenRepository.save(newToken);
        JWT.info("Tokens refreshed for user: {}", username);
        return newTokens;
    }

    private String generateToken(Map<String, Object> claims, UserDetails userDetails, long expiration) {
        Instant now = Instant.now();
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
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
            JWT.warn("Token expired: {}", ex.getMessage());
        } catch (Exception ex) {
            JWT.warn("Invalid token: {}", ex.getMessage());
        }
        return false;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
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
