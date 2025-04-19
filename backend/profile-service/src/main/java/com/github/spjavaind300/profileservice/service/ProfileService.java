package com.github.spjavaind300.profileservice.service;

import com.github.spjavaind300.profileservice.dto.*;
import com.github.spjavaind300.profileservice.dto.event.UserCreatedEvent;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProfileService {
    UserDTO getProfile(long userId);

    InternalUserResponse getUserForAdsRequest(long userId);

    InternalProfileResponse getProfileForAdsRequest(long userId);

    List<InternalUserSummary> getUserSummariesForAdsRequest(List<Long> userIds);

    UpdateUserDTO updateProfile(long userId, UpdateUserDTO updatedData);

    void deleteProfile (long targetUserId);

    String updateAvatar(long userId, MultipartFile file);

    void createProfile (UserCreatedEvent event);
}
