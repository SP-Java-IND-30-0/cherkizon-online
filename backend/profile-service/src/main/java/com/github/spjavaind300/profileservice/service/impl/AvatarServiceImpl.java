package com.github.spjavaind300.profileservice.service.impl;

import com.github.spjavaind300.profileservice.config.YandexConfig;
import com.github.spjavaind300.profileservice.service.AvatarService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.core.sync.RequestBody;


import java.io.IOException;

@Service
public class AvatarServiceImpl implements AvatarService {

    private final S3Client s3Client;
    private final String bucketName;

    public AvatarServiceImpl(S3Client s3Client, YandexConfig yandexConfig) {
        this.s3Client = s3Client;
        this.bucketName = yandexConfig.getBucketName();
    }


    @Override
    public String saveAvatar(MultipartFile avatar, Long id) throws IOException {
        String key = id + "/" + avatar.getOriginalFilename();

        if (!avatar.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Только изображения могут быть загружены.");
        }

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(avatar.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(avatar.getBytes()));

            return key;
        } catch (S3Exception e) {
            throw new RuntimeException("Ошибка загрузки аватара в S3: " +
                    e.awsErrorDetails().errorMessage(), e);
        }
    }

    @Override
    public void deleteAvatar(String avatarKey) {
        try {
            s3Client.deleteObject(deleteRequest -> deleteRequest.bucket(bucketName).key(avatarKey));
        } catch (S3Exception e) {
            throw new RuntimeException("Ошибка удаления аватара: " +
                    e.awsErrorDetails().errorMessage(), e);
        }
    }

}

