package com.github.spjavaind300.notificationservice.service.imp;

import com.github.spjavaind300.notificationservice.client.ProfileServiceClient;
import com.github.spjavaind300.notificationservice.model.dto.UserDto;
import com.github.spjavaind300.notificationservice.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImpTest {

    @Mock
    private ProfileServiceClient profileServiceClient;

    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        profileService = new ProfileServiceImp(profileServiceClient);
    }

    @Test
    void test_getProfiles() {

        UserDto user1 = new UserDto(1L,"user1_firstName", "user1_lastName", "user1@test.com");
        UserDto user2 = new UserDto(2L,"user2_firstName", "user2_lastName", "user2@test.com");

        when(profileServiceClient.getProfiles(List.of(1L,2L))).thenReturn(Set.of(user1,user2));

        List<UserDto> users = profileService.getProfiles(List.of(1L,2L));

        assertNotNull(users);
        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }
}