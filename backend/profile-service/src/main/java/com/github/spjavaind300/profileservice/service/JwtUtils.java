package com.github.spjavaind300.profileservice.service;

import com.github.spjavaind300.profileservice.dto.Role;

/**
 * Utility interface for generating and validating JSON Web Tokens (JWT).
 * <p>
 * Provides methods to generate a token for a given user ID and role,
 * extract user information and role from an existing token, and validate token integrity.
 */
public interface JwtUtils {

    /**
     * Validates the given JWT string.
     *
     * @param token the JWT string to validate
     * @return {@code true} if the token is correctly signed and not expired, {@code false} otherwise
     */
    boolean isValidToken(String token);

    /**
     * Extracts the user ID from the given JWT.
     *
     * @param token the JWT string
     * @return the user ID contained in the token subject
     */
    long getUserId(String token);

    /**
     * Extracts the user role from the given JWT.
     *
     * @param token the JWT string
     * @return the {@link Role} value stored in the token claims
     */
    Role getRole(String token);

}
