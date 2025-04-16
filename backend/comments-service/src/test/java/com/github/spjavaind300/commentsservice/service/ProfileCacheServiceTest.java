package com.github.spjavaind300.commentsservice.service;

import com.github.spjavaind300.commentsservice.cache.AuthorProfileCache;
import com.github.spjavaind300.commentsservice.cache.ProfileCacheServiceImpl;
import com.github.spjavaind300.commentsservice.dto.ProfileDto;
import com.github.spjavaind300.commentsservice.exception.ExternalServiceException;
import com.github.spjavaind300.commentsservice.exception.NotFoundException;
import com.github.spjavaind300.commentsservice.feing.ProfileFeignClientInternal;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileCacheServiceTest {

    @Mock
    private ProfileFeignClientInternal profileFeignClient;

    @Mock
    private AuthorProfileCache authorProfileCache;

    @InjectMocks
    private ProfileCacheServiceImpl profileCacheService;

    private final long authorId = 42L;

    private ProfileDto profile;

    @BeforeEach
    void setUp() {
        profile = new ProfileDto();
        profile.setAuthorId(authorId);
        profile.setAuthorFirstName("Анна");
        profile.setAuthorImage("anna.jpg");
    }

    @Test
    @DisplayName("Получение профиля из кэша — успех")
    void test_getProfile_fromCache() {
        when(authorProfileCache.get(authorId)).thenReturn(profile);

        ProfileDto result = profileCacheService.getProfile(authorId);

        assertNotNull(result);
        assertEquals("Анна", result.getAuthorFirstName());
        verifyNoInteractions(profileFeignClient);
    }

    @Test
    @DisplayName("Получение профиля из feign и добавление в кэш")
    void test_getProfile_fromFeign_andPutInCache() {
        when(authorProfileCache.get(authorId)).thenReturn(null);
        when(profileFeignClient.getProfileByIdInternal(authorId)).thenReturn(profile);

        ProfileDto result = profileCacheService.getProfile(authorId);

        assertNotNull(result);
        verify(profileFeignClient).getProfileByIdInternal(authorId);
        verify(authorProfileCache).put(authorId, profile);
    }

    @Test
    @DisplayName("Feign вернул 404 — NotFoundException")
    void test_getProfile_notFound() {
        when(authorProfileCache.get(authorId)).thenReturn(null);
        when(profileFeignClient.getProfileByIdInternal(authorId))
                .thenThrow(new FeignException.NotFound("Not found", Request.create(Request.HttpMethod.GET, "", Map.of(), null, null, null), null, null));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> profileCacheService.getProfile(authorId));

        assertEquals("ProfileDto с id=42 не найден", exception.getMessage());
    }

    @Test
    @DisplayName("Feign вернул ошибку — ExternalServiceException")
    void test_getProfile_otherFeignError() {
        when(authorProfileCache.get(authorId)).thenReturn(null);
        when(profileFeignClient.getProfileByIdInternal(authorId))
                .thenThrow(new FeignException.InternalServerError("Internal error", Request.create(Request.HttpMethod.GET, "", Map.of(), null, null, null), null, null));

        ExternalServiceException exception = assertThrows(ExternalServiceException.class, () -> profileCacheService.getProfile(authorId));

        assertEquals("Ошибка при обращении к внешнему сервису: Internal error", exception.getMessage());
    }
}