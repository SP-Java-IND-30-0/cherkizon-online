package com.github.spjavaind300.profileservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface AvatarService {

    String saveAvatar(MultipartFile avatar, Long id);

    void deleteAvatar(String avatarKey);

    byte[] getFile(String avatarKey);
}
