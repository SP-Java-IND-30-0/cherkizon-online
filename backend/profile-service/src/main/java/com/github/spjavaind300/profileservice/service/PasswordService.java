package com.github.spjavaind300.profileservice.service;

import com.github.spjavaind300.profileservice.dto.UpdatePasswordDTO;

public interface PasswordService {

    void changePassword(long userId, UpdatePasswordDTO passwordDTO);
}
