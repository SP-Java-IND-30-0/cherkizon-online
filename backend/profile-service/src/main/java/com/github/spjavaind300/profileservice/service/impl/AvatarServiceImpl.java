package com.github.spjavaind300.profileservice.service.impl;

import com.github.spjavaind300.profileservice.exception.AvatarStorageException;
import com.github.spjavaind300.profileservice.exception.AvatarReadException;
import com.github.spjavaind300.profileservice.service.AvatarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.core.sync.RequestBody;


import java.io.IOException;

@Service
@Slf4j
public class AvatarServiceImpl implements AvatarService {

    private final S3Client s3Client;
    private final String bucketName;

    public AvatarServiceImpl(S3Client s3Client, @Qualifier("bucketName") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }


    @Override
    public String saveAvatar(MultipartFile avatar, Long id) {
        String key = id + "/" + avatar.getOriginalFilename();

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(avatar.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(avatar.getBytes()));

            return key;

        } catch (IOException e) {
            throw new AvatarReadException("Ошибка чтения изображения", e);
        } catch (S3Exception e) {
            throw new AvatarStorageException("Ошибка загрузки аватара в S3: " +
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

    @Override
    public byte[] getFile(String avatarKey) {
        log.debug("getFile: пытаемся скачать объект из S3. bucket='{}', key='{}'", bucketName, avatarKey);
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(avatarKey)
                    .build();

            return s3Client.getObjectAsBytes(getObjectRequest).asByteArray();

        } catch (S3Exception e) {
            log.error("getFile: ошибка при получении объекта из S3. key='{}'", avatarKey, e);
            // можно обернуть в своё бизнес‑исключение
            throw e;
        } catch (Exception e) {
            log.error("getFile: непредвиденная ошибка при обработке key='{}'", avatarKey, e);
            throw e;
        }
    }


}
