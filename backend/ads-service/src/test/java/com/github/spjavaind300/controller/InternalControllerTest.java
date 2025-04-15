package com.github.spjavaind300.controller;

import com.github.spjavaind300.model.dto.AdForNotificationService;
import com.github.spjavaind300.model.entity.Ad;
import com.github.spjavaind300.repository.AdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InternalControllerTest {

    @LocalServerPort
    private int port;

    @Container
    private static final PostgreSQLContainer<?> postgres;

    static {
        postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17-alpine"))
                .withDatabaseName("db_test")
                .withUsername("test")
                .withPassword("test")
                .withInitScript("init_schema.sql");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private InternalController internalController;

    private String uri;

    @BeforeEach
    void setUp() {
        adRepository.deleteAll();
        uri = "http://localhost:" + port + "/internal/ads/";
    }

    @Test
    void contextLoads() {
        assertNotNull(internalController);
    }


    @Test
    void test_getAdv_success() {
        Ad ad = new Ad(0, "test_ad", 100, "description", 1L, "key", "image.png");

        Ad saved = adRepository.save(ad);

        ResponseEntity<AdForNotificationService> response
                = restTemplate.getForEntity(uri + ad.getId(), AdForNotificationService.class);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertTrue(response.getStatusCode().is2xxSuccessful());

        AdForNotificationService expected = response.getBody();

        assertEquals(saved.getTitle(), expected.title());
        assertEquals(saved.getId(), expected.id());
    }


    @Test
    void test_getAdv_whenNotFound_thenThrows() {
        Ad ad = new Ad(0, "test_ad", 100, "description", 1L, "key", "image.png");

        adRepository.save(ad);

        ResponseEntity<AdForNotificationService> response
                = restTemplate.getForEntity(uri + ad.getId() + 1, AdForNotificationService.class);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertTrue(response.getStatusCode().is5xxServerError()); // TODO переделать после добавления ControllerAdvice

    }
}