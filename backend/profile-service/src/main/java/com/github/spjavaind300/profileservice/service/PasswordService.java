package com.github.spjavaind300.profileservice.service;

import com.github.spjavaind300.profileservice.dto.UpdatePasswordDTO;

/**
 * Service for changing user passwords.
 */
public interface PasswordService {

    /**
     * Changes the password for the specified user.
     *
     * @param userId      the unique identifier of the user whose password is to be changed
     * @param passwordDTO an {@link UpdatePasswordDTO} containing the old and new passwords
     */
    void changePassword(long userId, UpdatePasswordDTO passwordDTO);
}
