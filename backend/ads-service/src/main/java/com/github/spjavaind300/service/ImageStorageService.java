package com.github.spjavaind300.service;

import com.github.spjavaind300.model.dto.ImageDto;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

public interface ImageStorageService {

    ImageDto uploadFile(MultipartFile file);

    String getPreSignedUrl(String imageKey, Duration expiration);

    byte[] getFile(String imageKey);

    void deleteFile(String imageKey);
}
