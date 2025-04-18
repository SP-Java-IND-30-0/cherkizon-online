package com.github.spjavaind300.profileservice.service;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;

public interface AvatarService {

    String saveAvatar(MultipartFile avatar, Long id);

    void deleteAvatar(String avatarKey);

    public byte[] getFile(String imageKey);
}
