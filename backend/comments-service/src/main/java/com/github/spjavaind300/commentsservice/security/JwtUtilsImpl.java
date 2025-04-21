package com.github.spjavaind300.commentsservice.security;

import com.github.spjavaind300.commentsservice.exception.InvalidJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import com.github.spjavaind300.commentsservice.dto.Role;
import com.github.spjavaind300.commentsservice.dto.UserContext;
import com.github.spjavaind300.commentsservice.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.function.Function;

/**
 * Implementation of the {@link JwtUtils} interface that provides utilities for working with JWT tokens.
 * Supports extracting user information and roles, validating tokens, and handling security-related exceptions.
 */
@Service
@RequiredArgsConstructor
public class JwtUtilsImpl implements JwtUtils {

    private final String JwtSecret;

    /**
     * Retrieves the current user's context from the thread-local storage.
     *
     * @return the current {@link UserContext}
     * @throws UnauthorizedException if no user context is available (e.g., unauthenticated request)
     */
    @Override
    public UserContext getUserContext() {
        UserContext context = UserContextHolder.getContext();
        if (context == null) {
            throw new UnauthorizedException();
        }
        return context;
    }

    /**
     * Extracts the user ID from the given JWT token.
     *
     * @param token the JWT token
     * @return the user ID as a long
     * @throws InvalidJwtException if the token is invalid or cannot be parsed
     */
    @Override
    public long getUserId(String token) {
        try {
            return Long.parseLong(extractClaim(token, Claims::getSubject));
        } catch (Exception e) {
            throw new InvalidJwtException(e);
        }
    }

    /**
     * Extracts the user's role from the given JWT token.
     *
     * @param token the JWT token
     * @return the {@link Role} of the user
     * @throws InvalidJwtException if the token is invalid or the role cannot be resolved
     */
    @Override
    public Role getRole(String token) {
        try {
            return Role.valueOf(extractClaim(token, claims -> claims.get("role", String.class)));
        } catch (Exception e) {
            throw new InvalidJwtException(e);
        }
    }

    /**
     * Validates the integrity and structure of the provided JWT token.
     *
     * @param token the JWT token
     * @return true if the token is valid; false otherwise
     */
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

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }
}