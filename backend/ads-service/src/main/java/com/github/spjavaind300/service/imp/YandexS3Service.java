package com.github.spjavaind300.service.imp;

import com.github.spjavaind300.model.dto.ImageDto;
import com.github.spjavaind300.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class YandexS3Service implements ImageStorageService {

    private static final String DEFAULT_IMAGE_KEY = "images/default.png";
    private static final String DEFAULT_IMAGE_NAME = "default.png";
    private static final String IMAGE_KEY = "images/";
    private static final Set<String> ALLOW_CONTENT_TYPES = Set.of("image/png", "image/jpeg", "image/jpg");

    private final S3Client s3Client;

    private final S3Presigner s3Presigner;

    @Value("${s3.bucket-name}")
    private String bucketName;


    @Override
    public ImageDto uploadFile(MultipartFile file) {
        String imageKey = IMAGE_KEY + UUID.randomUUID() + "." + FilenameUtils.getExtension(file.getOriginalFilename());

        try {
            if (!ALLOW_CONTENT_TYPES.contains(file.getContentType())) {
                throw new IllegalArgumentException("Invalid content type");
            }
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(imageKey)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
            return new ImageDto(imageKey, file.getOriginalFilename());
        } catch (IOException | IllegalArgumentException e) {
            log.warn("Failed to upload file {}, reason {}", file.getOriginalFilename(), e.getMessage());
            return new ImageDto(DEFAULT_IMAGE_KEY, DEFAULT_IMAGE_NAME);
        }

    }

    @Override
    public String getPreSignedUrl(String imageKey, Duration expiration) {

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .getObjectRequest(r -> r.bucket(bucketName).key(imageKey))
                .build();

        return s3Presigner.presignGetObject(getObjectPresignRequest).url().toString();
    }

    @Override
    public byte[] getFile(String imageKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(imageKey)
                .build();

        return s3Client.getObjectAsBytes(getObjectRequest).asByteArray();
    }

    @Override
    public void deleteFile(String imageKey) {
        if (imageKey.equals(DEFAULT_IMAGE_KEY)) {
            return;
        }
        try {
            s3Client.deleteObject(r -> r.bucket(bucketName).key(imageKey));
        } catch (S3Exception e) {
            log.warn("Failed to delete file {}, reason {}", imageKey, e.getMessage());
        }
    }


}
