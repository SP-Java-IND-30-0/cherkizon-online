package com.github.spjavaind300.profileservice.security;

import com.github.spjavaind300.profileservice.dto.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for authenticating the application itself
 * as a service user within the security context.
 * <p>
 * This allows internal service-to-service calls to be performed
 * with SERVICE-level privileges.
 */
@Service
public class SecurityService {

    /**
     * Sets up service-level authentication in the security context
     * for the given service user ID.
     * <p>
     * Creates a {@link CustomUserDetails} instance with the SERVICE role
     * and stores it as the current {@link org.springframework.security.core.Authentication}.
     *
     * @param id the unique identifier of the service user
     */
    public void serviceAuthentication(long id) {
        CustomUserDetails serviceUser = new CustomUserDetails(id, Role.SERVICE);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        serviceUser,
                        null,
                        serviceUser.getAuthorities()
                )
        );
    }
}


