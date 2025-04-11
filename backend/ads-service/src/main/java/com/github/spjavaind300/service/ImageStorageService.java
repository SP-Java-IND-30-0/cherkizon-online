package com.github.spjavaind300.service;

import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

public interface ImageStorageService {

    String uploadFile(MultipartFile file);

    String getPreSignedUrl(String imageKey, Duration expiration);
}
