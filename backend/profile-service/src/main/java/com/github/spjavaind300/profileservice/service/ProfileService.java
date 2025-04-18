package com.github.spjavaind300.profileservice.service;

import com.github.spjavaind300.profileservice.dto.UpdateUserDTO;
import com.github.spjavaind300.profileservice.dto.UserDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProfileService {
    UserDTO getProfile(long userId);

    UpdateUserDTO updateProfile(long userId, UpdateUserDTO updatedData);

    void deleteProfile (long targetUserId);

    String updateAvatar(long userId, MultipartFile file);
}
