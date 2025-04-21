package com.github.spjavaind300.profileservice.security;


import com.github.spjavaind300.profileservice.dto.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * UserDetails implementation that represents an authenticated user or service.
 *
 * Holds the user ID and role, and exposes Spring Security authorities based on the role.
 */
public record CustomUserDetails(long userId, Role role) implements UserDetails {

    /**
     * Returns a collection of authorities granted to the user.
     *
     * @return a list containing a single {@link SimpleGrantedAuthority} derived from the user's role
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * Returns the password used to authenticate the user.
     *
     * @return an empty string as credentials are not stored here
     */
    @Override
    public String getPassword() {
        return "";
    }

    /**
     * Returns the username used to authenticate the user.
     *
     * @return an empty string as username is not stored here
     */
    @Override
    public String getUsername() {
        return "";
    }
}
