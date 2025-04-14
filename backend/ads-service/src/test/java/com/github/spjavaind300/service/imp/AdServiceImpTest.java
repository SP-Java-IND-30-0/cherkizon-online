package com.github.spjavaind300.service.imp;

import com.github.spjavaind300.exception.NotFoundException;
import com.github.spjavaind300.model.dto.AdExtraInfoDto;
import com.github.spjavaind300.model.dto.AdRequestDto;
import com.github.spjavaind300.model.dto.AdResponseDto;
import com.github.spjavaind300.model.dto.ImageDto;
import com.github.spjavaind300.model.dto.ListAdsDto;
import com.github.spjavaind300.model.dto.UserDto;
import com.github.spjavaind300.model.entity.Ad;
import com.github.spjavaind300.model.mapper.AdMapper;
import com.github.spjavaind300.repository.AdRepository;
import com.github.spjavaind300.service.AdService;
import com.github.spjavaind300.service.ImageStorageService;
import com.github.spjavaind300.service.ProfileService;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
class AdServiceImpTest {

    @Container
    private static final PostgreSQLContainer<?> postgres;

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private AdMapper adMapper;

    @MockitoBean
    private ImageStorageService imageStorageService;

    @MockitoBean
    private ProfileService profileService;

    private AdService adService;

    static {
        postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17-alpine"))
                .withDatabaseName("db_test")
                .withUsername("test")
                .withPassword("test")
                .withInitScript("init_schema.sql");
    }

    @BeforeEach
    void setUp() {
        adService = new AdServiceImp(adRepository, adMapper, imageStorageService, profileService);

    }

    @AfterEach
    void tearDown() {
        adRepository.deleteAll();
    }


    @Test
    void test_getAllAds_withEmptyRepository_returnsEmptyList() {

        ListAdsDto actual = adService.getAllAds();

        assertNotNull(actual);
        assertEquals(0, actual.getCount());
        assertTrue(actual.getItems().isEmpty());
    }

    @Test
    void test_getAllAds_withAdsExist_returnsListOfAds() {

        Ad ad1 = new Ad(0, "test_ad1", 100, "description ad1", 1L, "key1", "image1.png");
        adRepository.save(ad1);
        Ad ad2 = new Ad(0, "test_ad2", 120, "description ad2", 2L, "key2", "image2.png");
        adRepository.save(ad2);

        ListAdsDto actual = adService.getAllAds();

        assertNotNull(actual);
        assertEquals(2, actual.getCount());
        assertTrue(actual.getItems().stream().anyMatch(ad -> ad.getTitle().equals("test_ad1")));
        assertTrue(actual.getItems().stream().anyMatch(ad -> ad.getTitle().equals("test_ad2")));

    }

    @Test
    void test_getAllAdsForUser_whenHasNotExist_returnsEmptyList() {

        Ad ad1 = new Ad(0, "test_ad1", 100, "description ad1", 1L, "key1", "image1.png");
        adRepository.save(ad1);
        Ad ad2 = new Ad(0, "test_ad2", 120, "description ad2", 2L, "key2", "image2.png");
        adRepository.save(ad2);

        ListAdsDto actual = adService.getAllAdsForUser(3L);

        assertNotNull(actual);
        assertEquals(0, actual.getCount());
        assertTrue(actual.getItems().isEmpty());

    }

    @Test
    void test_getAllAdsForUser_whenAdsExist_returnsListOfAds() {

        Ad ad1 = new Ad(0, "test_ad1", 100, "description ad1", 1L, "key1", "image1.png");
        adRepository.save(ad1);
        Ad ad2 = new Ad(0, "test_ad2", 120, "description ad2", 2L, "key2", "image2.png");
        adRepository.save(ad2);

        ListAdsDto actual = adService.getAllAdsForUser(1L);

        assertNotNull(actual);
        assertEquals(1, actual.getCount());
        assertTrue(actual.getItems().stream().anyMatch(ad -> ad.getTitle().equals("test_ad1")));

    }

    @Test
    void test_getAdInfo_success() {
        Ad ad1 = new Ad(0, "test_ad1", 100, "description ad1", 1L, "key1", "image1.png");
        Ad savedAd1 = adRepository.save(ad1);
        UserDto userDto = new UserDto("Author1_FirstName", "Author1_LastName", "author1@test.com", "phone1");
        when(profileService.getUser(savedAd1.getUserId())).thenReturn(userDto);

        AdExtraInfoDto actual = adService.getAdInfo(savedAd1.getId());

        assertNotNull(actual);
        assertEquals(ad1.getTitle(), actual.getTitle());
        assertEquals(userDto.authorFirstName(), actual.getAuthorFirstName());
    }

    @Test
    void test_getAdInfo_whenAdNotExist_shouldThrowException() {
        Ad ad1 = new Ad(0, "test_ad1", 100, "description ad1", 1L, "key1", "image1.png");
        adRepository.save(ad1);

        assertThrows(NotFoundException.class, () -> adService.getAdInfo(0));
    }

    @Test
    void test_getAdInfo_whenUserNotExist_shouldThrowException() {
        Ad ad1 = new Ad(0, "test_ad1", 100, "description ad1", 1L, "key1", "image1.png");
        Ad savedAd1 = adRepository.save(ad1);
        when(profileService.getUser(savedAd1.getUserId()))
                .thenThrow(new FeignException.FeignClientException.NotFound(
                        "404",
                        Request.create(
                                Request.HttpMethod.GET,
                                "url",
                                new HashMap<>(),
                                new byte[0],
                                null,
                                new RequestTemplate()),
                        null,
                        null
                ));
        assertThrows(FeignException.FeignClientException.NotFound.class, () -> adService.getAdInfo(ad1.getId()));
    }

    @Test
    void test_createAd() {
        AdRequestDto requestDto = new AdRequestDto("test title 1", 100, "description 1");
        MockMultipartFile image = new MockMultipartFile("image1.png", "test image".getBytes());
        ImageDto imageDto = new ImageDto("key1", "image1.png");

        when(imageStorageService.uploadFile(image)).thenReturn(imageDto);
        when(imageStorageService.getPreSignedUrl(any(String.class), any(Duration.class))).thenReturn("/test-image-url");

        AdResponseDto actual = adService.createAd(1L, requestDto, image);

        assertNotNull(actual);
        assertEquals(requestDto.title(), actual.getTitle());
        assertEquals("/test-image-url", actual.getImage());

    }

    @Test
    void test_deleteAd() {
        Ad ad1 = new Ad(0, "test_ad1", 100, "description ad1", 1L, "key1", "image1.png");
        Ad savedAd1 = adRepository.save(ad1);

        assertEquals(1, adRepository.count());

        adService.deleteAd(savedAd1.getId());

        assertEquals(0, adRepository.count());
    }

    @Test
    void test_updateAd_success() {

        Ad ad1 = new Ad(0, "test_ad1", 100, "description ad1", 1L, "key1", "image1.png");
        Ad savedAd1 = adRepository.save(ad1);

        AdRequestDto requestDto = new AdRequestDto("test title 1", 100, "description 1");

        AdResponseDto actual = adService.updateAd(savedAd1.getId(), requestDto);

        assertNotNull(actual);
        assertEquals(requestDto.title(), actual.getTitle());

    }

    @Test
    void test_updateAd_thenAdNotFound_shouldThrowException() {

        Ad ad1 = new Ad(0, "test_ad1", 100, "description ad1", 1L, "key1", "image1.png");
        adRepository.save(ad1);

        AdRequestDto requestDto = new AdRequestDto("test title 1", 100, "description 1");

        assertThrows(NotFoundException.class, () -> adService.updateAd(0, requestDto));

    }

    @Test
    void test_updateImage_success() {
        Ad ad1 = new Ad(0, "test_ad1", 100, "description ad1", 1L, "key1", "image1.png");
        Ad savedAd1 = adRepository.save(ad1);

        MockMultipartFile image = new MockMultipartFile("file", "new_image1.png", "image/png","test image".getBytes());
        ImageDto imageDto = new ImageDto("new_key1", "new_image1.png");
        when(imageStorageService.uploadFile(image)).thenReturn(imageDto);
        when(imageStorageService.getFile(imageDto.url())).thenReturn("test image".getBytes());

        byte[] actual = adService.updateImage(savedAd1.getId(), image);

        Ad updatedAd = adRepository.findById(savedAd1.getId()).orElseThrow();

        assertNotNull(actual);
        assertArrayEquals("test image".getBytes(), actual);

        assertEquals("new_key1", updatedAd.getImageKey());
        assertEquals("new_image1.png", updatedAd.getOriginalFilename());
    }

    @Test
    void test_updateImage_whenAdNotExist_shouldThrowException() {
        Ad ad1 = new Ad(0, "test_ad1", 100, "description ad1", 1L, "key1", "image1.png");
        adRepository.save(ad1);

        MockMultipartFile image = new MockMultipartFile("image1.png", "test image".getBytes());

        assertThrows(NotFoundException.class, () -> adService.updateImage(0, image));
    }

}