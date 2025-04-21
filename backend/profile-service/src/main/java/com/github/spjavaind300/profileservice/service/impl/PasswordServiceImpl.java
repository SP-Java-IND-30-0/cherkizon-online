package com.github.spjavaind300.profileservice.service.impl;

import com.github.spjavaind300.profileservice.client.AuthClient;
import com.github.spjavaind300.profileservice.dto.UpdatePasswordDTO;
import com.github.spjavaind300.profileservice.exception.InvalidNewPasswordException;
import com.github.spjavaind300.profileservice.service.PasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link PasswordService} that delegates password changes
 * to the authentication client.
 */
@Service
public class PasswordServiceImpl implements PasswordService {

    private final AuthClient authClient;

    public PasswordServiceImpl(AuthClient authClient) {
        this.authClient = authClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changePassword(long userId, UpdatePasswordDTO passwordDTO) {
        ResponseEntity<Void> response = authClient.changePassword(userId, passwordDTO);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new InvalidNewPasswordException("New password is invalid");
        }
    }
}
