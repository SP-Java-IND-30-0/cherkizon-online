package com.github.spjavaind300.profileservice.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service for handling user avatar storage and retrieval.
 * <p>
 * Provides methods to save avatar images to storage, delete them,
 * and fetch avatar bytes for a given user.
 */
public interface AvatarService {

    /**
     * Saves the provided avatar image for the specified user.
     *
     * @param avatar the avatar image file to save
     * @param id     the unique identifier of the user
     * @return the storage key under which the avatar is saved
     */
    String saveAvatar(MultipartFile avatar, Long id);

    /**
     * Deletes the avatar image identified by the given storage key.
     *
     * @param avatarKey the storage key of the avatar to delete
     */
    void deleteAvatar(String avatarKey);

    /**
     * Retrieves the avatar image bytes for the specified user.
     *
     * @param userId the unique identifier of the user whose avatar to fetch
     * @return a byte array containing the avatar image
     */
    byte[] getFile(long userId);
}
