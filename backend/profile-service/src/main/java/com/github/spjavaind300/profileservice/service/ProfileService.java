package com.github.spjavaind300.profileservice.service;

import com.github.spjavaind300.profileservice.dto.*;
import com.github.spjavaind300.profileservice.dto.event.UserCreatedEvent;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for managing user profiles.
 * <p>
 * Provides operations to retrieve, update, and delete user profiles,
 * handle avatar uploads, and react to user lifecycle events.
 */
public interface ProfileService {

    /**
     * Retrieves the profile information for a given user.
     *
     * @param userId the unique identifier of the user
     * @return a {@link UserDTO} containing the user's profile details
     */
    UserDTO getProfile(long userId);

    /**
     * Retrieves user data formatted for advertising requests.
     *
     * @param userId the unique identifier of the user
     * @return an {@link InternalUserResponse} suitable for ad service consumption
     */
    InternalUserResponse getUserForAdsRequest(long userId);

    /**
     * Retrieves profile data formatted for advertising requests.
     *
     * @param userId the unique identifier of the user
     * @return an {@link InternalProfileResponse} suitable for ad service consumption
     */
    InternalProfileResponse getProfileForAdsRequest(long userId);

    /**
     * Retrieves summaries for multiple users formatted for advertising requests.
     *
     * @param userIds a list of user IDs to fetch summaries for
     * @return a {@link List} of {@link InternalUserSummary} objects
     */
    List<InternalUserSummary> getUserSummariesForAdsRequest(List<Long> userIds);

    /**
     * Updates the profile information for a given user.
     *
     * @param userId      the unique identifier of the user to update
     * @param updatedData a {@link UpdateUserDTO} containing the new profile values
     * @return an {@link UpdateUserDTO} reflecting the updated profile data
     */
    UpdateUserDTO updateProfile(long userId, UpdateUserDTO updatedData);

    /**
     * Deletes the profile of the specified user.
     *
     * @param targetUserId the unique identifier of the user to delete
     */
    void deleteProfile(long targetUserId);

    /**
     * Updates the avatar image for a given user.
     *
     * @param userId the unique identifier of the user
     * @param file   the new avatar image file to upload
     * @return the URL of the uploaded avatar image
     */
    String updateAvatar(long userId, MultipartFile file);

    /**
     * Creates a user profile in response to a user creation event.
     *
     * @param event the {@link UserCreatedEvent} containing the initial user data
     */
    void createProfile(UserCreatedEvent event);
}

