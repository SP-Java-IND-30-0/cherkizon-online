package com.github.spjavaind300.service.imp;

import com.github.spjavaind300.client.ProfileServiceClient;
import com.github.spjavaind300.exception.ProfileRequestFailedException;
import com.github.spjavaind300.model.dto.UserDto;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImpTest {

    @Mock
    private ProfileServiceClient client;

    @InjectMocks
    private ProfileServiceImp profileService;

    private final UserDto testUser = new UserDto(
            "Author FirstName",
            "Author LastName",
            "test@test.com",
            "+79999999999");


    @Test
    void test_getUser_shouldReturnUser_whenClientReturnsSuccess() {
        long userId = 1L;
        when(client.getProfiles(userId)).thenReturn(testUser);

        UserDto result = profileService.getUser(userId);

        verify(client).getProfiles(userId);
        assertThat(result).isEqualTo(testUser);
    }

    @Test
    void test_getUser_shouldThrowException_whenClientThrows() {
        long userId = 2L;
        when(client.getProfiles(userId)).thenThrow(FeignException.FeignClientException.InternalServerError.class);

        assertThatThrownBy(() -> profileService.getUser(userId))
                .isInstanceOf(FeignException.class);
    }

    @Test
    void getUserFallback_shouldWrapException() {
        long userId = 2L;
        Throwable cause = new FeignException.NotFound("404",
                Request.create(
                        Request.HttpMethod.GET,
                        "url",
                        new HashMap<>(),
                        new byte[0],
                        null,
                        new RequestTemplate()),
                new byte[0],
                new HashMap<>());

        assertThatThrownBy(() -> profileService.getUserFallback(userId, cause))
                .isInstanceOf(ProfileRequestFailedException.class)
                .hasMessageContaining("Failed to get user with id=" + userId)
                .hasCause(cause);
    }
}