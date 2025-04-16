package com.github.spjavaind300.service.imp;

import com.github.spjavaind300.SecurityTestUtils;
import com.github.spjavaind300.exception.AccessDeniedException;
import com.github.spjavaind300.exception.NotFoundException;
import com.github.spjavaind300.model.dto.AdExtraInfoDto;
import com.github.spjavaind300.model.dto.AdRequestDto;
import com.github.spjavaind300.model.dto.AdResponseDto;
import com.github.spjavaind300.model.dto.ImageDto;
import com.github.spjavaind300.model.dto.ListAdsDto;
import com.github.spjavaind300.model.dto.Role;
import com.github.spjavaind300.model.dto.UserDto;
import com.github.spjavaind300.model.entity.Ad;
import com.github.spjavaind300.model.event.AdEvent;
import com.github.spjavaind300.model.mapper.AdMapper;
import com.github.spjavaind300.repository.AdRepository;
import com.github.spjavaind300.service.AdService;
import com.github.spjavaind300.service.ImageStorageService;
import com.github.spjavaind300.service.OutboxEventService;
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

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    @MockitoBean
    private OutboxEventService outboxEventService;

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

        SecurityTestUtils.setupMockUser(1L, Role.USER);

        adService = new AdServiceImp(adRepository, adMapper, imageStorageService, profileService, outboxEventService);


    }

    @AfterEach
    void tearDown() {
        SecurityTestUtils.clearSecurityContext();
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

        AdResponseDto actual = adService.createAd(1L, requestDto, image);

        assertNotNull(actual);
        assertEquals(requestDto.title(), actual.getTitle());
        assertEquals(imageDto.url(), actual.getImage());

    }

    @Test
    void test_deleteAd_whenUserIsOwner_success() {
        Ad ad1 = new Ad(0, "test_ad1", 100, "description ad1", 1L, "key1", "image1.png");
        Ad savedAd1 = adRepository.save(ad1);

        assertEquals(1, adRepository.count());

        adService.deleteAd(savedAd1.getId());

        assertEquals(0, adRepository.count());
    }

    @Test
    void test_deleteAd_whenUserIsNotOwner_thenThrows() {
        Ad ad1 = new Ad(0, "test_ad1", 100, "description ad1", 5L, "key1", "image1.png");
        Ad savedAd1 = adRepository.save(ad1);

        assertEquals(1, adRepository.count());

        assertThrows(AccessDeniedException.class, () -> adService.deleteAd(savedAd1.getId()));

    }

    @Test
    void test_deleteAd_whenAdminRole_success() {
        Ad ad1 = new Ad(0, "test_ad1", 100, "description ad1", 1L, "key1", "image1.png");
        Ad savedAd1 = adRepository.save(ad1);

        assertEquals(1, adRepository.count());

        adService.deleteAd(savedAd1.getId());

        assertEquals(0, adRepository.count());
    }

    @Test
    void test_deleteAd_whenServiceRole_success() {
        Ad ad1 = new Ad(0, "test_ad1", 100, "description ad1", 1L, "key1", "image1.png");
        Ad savedAd1 = adRepository.save(ad1);


        assertEquals(1, adRepository.count());
        doNothing().when(outboxEventService).saveOutboxEvent(any(AdEvent.class));

        adService.deleteAd(savedAd1.getId());

        assertEquals(0, adRepository.count());
        verify(outboxEventService, times(1)).saveOutboxEvent(any(AdEvent.class));
    }

    @Test
    void test_updateAd_whenUserIsOwner_success() {

        Ad ad1 = new Ad(0, "test_ad1", 100, "description ad1", 1L, "key1", "image1.png");
        Ad savedAd1 = adRepository.save(ad1);

        AdRequestDto requestDto = new AdRequestDto("test title 1", 100, "description 1");

        AdResponseDto actual = adService.updateAd(savedAd1.getId(), requestDto);

        assertNotNull(actual);
        assertEquals(requestDto.title(), actual.getTitle());

    }

    @Test
    void test_updateAd_whenUserIsNotOwner_thenThrows() {

        Ad ad1 = new Ad(0, "test_ad1", 100, "description ad1", 5L, "key1", "image1.png");
        Ad savedAd1 = adRepository.save(ad1);

        AdRequestDto requestDto = new AdRequestDto("test title 1", 100, "description 1");

        assertThrows(AccessDeniedException.class, () -> adService.updateAd(savedAd1.getId(), requestDto));

    }

    @Test
    void test_updateAd_whenAdminRole_success() {

        Ad ad1 = new Ad(0, "test_ad1", 100, "description ad1", 1L, "key1", "image1.png");
        Ad savedAd1 = adRepository.save(ad1);

        AdRequestDto requestDto = new AdRequestDto("test title 1", 100, "description 1");

        AdResponseDto actual = adService.updateAd(savedAd1.getId(), requestDto);

        assertNotNull(actual);
        assertEquals(requestDto.title(), actual.getTitle());

    }

    @Test
    void test_updateAd_whenServiceRole_success() {

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
    void test_updateImage_whenUserIsOwner_success() {
        Ad ad1 = new Ad(0, "test_ad1", 100, "description ad1", 1L, "key1", "image1.png");
        Ad savedAd1 = adRepository.save(ad1);

        MockMultipartFile image = new MockMultipartFile("file", "new_image1.png", "image/png", "test image".getBytes());
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
    void test_updateImage_whenUserIsNotOwner_thenThrows() {
        Ad ad1 = new Ad(0, "test_ad1", 100, "description ad1", 5L, "key1", "image1.png");
        Ad savedAd1 = adRepository.save(ad1);

        MockMultipartFile image = new MockMultipartFile("file", "new_image1.png", "image/png", "test image".getBytes());
        ImageDto imageDto = new ImageDto("new_key1", "new_image1.png");
        when(imageStorageService.uploadFile(image)).thenReturn(imageDto);
        when(imageStorageService.getFile(imageDto.url())).thenReturn("test image".getBytes());

        assertThrows(AccessDeniedException.class, () -> adService.updateImage(savedAd1.getId(), image));

    }

    @Test
    void test_updateImage_whenAdminRole_success() {
        Ad ad1 = new Ad(0, "test_ad1", 100, "description ad1", 1L, "key1", "image1.png");
        Ad savedAd1 = adRepository.save(ad1);

        MockMultipartFile image = new MockMultipartFile("file", "new_image1.png", "image/png", "test image".getBytes());
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
    void test_updateImage_whenServiceRole_success() {
        Ad ad1 = new Ad(0, "test_ad1", 100, "description ad1", 1L, "key1", "image1.png");
        Ad savedAd1 = adRepository.save(ad1);

        MockMultipartFile image = new MockMultipartFile("file", "new_image1.png", "image/png", "test image".getBytes());
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

    @Test
    void test_deleteAllByUserId_success() {
        Ad ad1 = new Ad(0, "test_ad1", 100, "description ad1", 1L, "key1", "image1.png");
        adRepository.save(ad1);
        Ad ad2 = new Ad(0, "test_ad2", 120, "description ad2", 1L, "key2", "image2.png");
        adRepository.save(ad2);
        Ad ad3 = new Ad(0, "test_ad3", 130, "description ad3", 2L, "key3", "image3.png");
        adRepository.save(ad3);

        assertEquals(3, adRepository.count());

        adService.deleteAllByUserId(1L);

        assertEquals(1, adRepository.count());
        verify(imageStorageService, times(2)).deleteFile(any(String.class));

    }

    @Test
    void test_deleteAllByUserId_whenUserHasNotAds() {
        Ad ad3 = new Ad(0, "test_ad3", 130, "description ad3", 2L, "key3", "image3.png");
        adRepository.save(ad3);
        assertEquals(1, adRepository.count());

        adService.deleteAllByUserId(1L);

        assertEquals(1, adRepository.count());
        verify(imageStorageService, never()).deleteFile(any(String.class));

    }


}