package com.github.spjavaind300.profileservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AvatarService {

    String saveAvatar(MultipartFile avatar, Long id) throws IOException;

    void deleteAvatar(String avatarKey);
}
