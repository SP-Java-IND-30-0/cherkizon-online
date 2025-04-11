package com.github.spjavaind300.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class YandexS3ServiceTest {

    private static final String DEFAULT_IMAGE_KEY = "images/default.png";

    @Autowired
    private S3Client s3Client;

    @Autowired
    private YandexS3Service yandexS3Service;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${s3.bucket-name}")
    private String bucketName;

    private String uploadKey;
    private MultipartFile testFile;

    @BeforeEach
    void setUp() throws IOException {
        Path tempFile = Files.createTempFile("test-upload-", ".jpg");
        Files.write(tempFile, "This is a test image".getBytes());
        String testFileName = "test.jpg";
        this.testFile = new MockMultipartFile(
                "file",
                testFileName,
                "image/jpeg",
                Files.newInputStream(tempFile)
        );
    }

    @AfterEach
    void tearDown() {
        if (uploadKey != null) {
            try {
                s3Client.deleteObject(b -> b.bucket(bucketName).key(uploadKey));
            } catch (S3Exception e) {
                log.error("failed to delete test file: {}", e.getMessage());
            }
        }
    }

    @Test
    void test_uploadFile_successAndReturnKey() {
        uploadKey = yandexS3Service.uploadFile(testFile);

        HeadObjectResponse response = s3Client.headObject(b -> b.bucket(bucketName).key(uploadKey));

        assertNotNull(response);
        assertNotNull(uploadKey);

        assertTrue(uploadKey.startsWith("images/"));
        assertTrue(response.contentType().startsWith("image/jpeg"));

        String url = yandexS3Service.getPreSignedUrl(uploadKey, Duration.ofMinutes(5));

        assertTrue(url.contains(bucketName));
        assertTrue(url.contains(uploadKey));

        URI uri = URI.create(url);

        ResponseEntity<byte[]> responseEntity = restTemplate.getForEntity(uri, byte[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().length > 0);

    }

    @Test
    void test_uploadFile_failAndReturnDefaultKey() throws IOException {

        MultipartFile invalidFile = new MockMultipartFile(
                "file",
                "invalid.txt",
                "text/plain",
                (InputStream) null
        );

        String result = yandexS3Service.uploadFile(invalidFile);

        assertEquals(DEFAULT_IMAGE_KEY, result);
    }

}