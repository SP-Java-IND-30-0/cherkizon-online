package com.github.spjavaind300.profileservice.service;

import com.github.spjavaind300.profileservice.dto.UpdateUserDTO;
import com.github.spjavaind300.profileservice.dto.UserDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProfileService {
    UserDTO getProfile(Long userId);

    UpdateUserDTO updateProfile(Long userId, UpdateUserDTO updatedData);

    void deleteProfile (Long targetUserId, String token);

    String saveOrUpdateAvatar(Long userId, MultipartFile file) throws IOException;

}
