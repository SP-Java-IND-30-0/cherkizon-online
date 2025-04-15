package com.github.spjavaind300.profileservice.service;

import com.github.spjavaind300.profileservice.dto.JwtUserInfo;
import com.github.spjavaind300.profileservice.dto.UpdateUserDTO;
import com.github.spjavaind300.profileservice.dto.UserDTO;
import com.github.spjavaind300.profileservice.exception.UserNotFoundAuthException;
import com.github.spjavaind300.profileservice.mapper.UserMapper;
import com.github.spjavaind300.profileservice.model.entity.User;
import com.github.spjavaind300.profileservice.repository.UserRepository;
import com.github.spjavaind300.profileservice.service.impl.AvatarServiceImpl;
import com.github.spjavaind300.profileservice.service.impl.JwtServiceImpl;
import com.github.spjavaind300.profileservice.service.impl.ProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.MockitoAnnotations.*;

import java.io.IOException;
import java.util.Optional;

class ProfileServiceImplTest {

    @Mock
    private AvatarServiceImpl avatarService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtServiceImpl jwtService;

    @InjectMocks
    private ProfileServiceImpl profileService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void getProfile_whenUserExists_returnsUserDTO() {
        Long userId = 1L;
        User mockUser = new User();
        UserDTO mockUserDTO = new UserDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userMapper.toUserDTO(mockUser)).thenReturn(mockUserDTO);

        UserDTO result = profileService.getProfile(userId);

        assertThat(result).isEqualTo(mockUserDTO);
        verify(userRepository).findById(userId);
        verify(userMapper).toUserDTO(mockUser);
    }

    @Test
    void getProfile_whenUserNotFound_throwsException() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.getProfile(userId))
                .isInstanceOf(UserNotFoundAuthException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void updateProfile_whenUserExists_updatesAndReturnsUpdateUserDTO() {
        Long userId = 1L;

        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setFirstName("Артём");
        updateUserDTO.setLastName("Акопян");
        updateUserDTO.setPhone("+79996311972");

        User existingUser = new User();
        existingUser.setId(userId);

        User updatedUser = new User();
        updatedUser.setFirstName("Артём");
        updatedUser.setLastName("Акопян");
        updatedUser.setPhone("+79996311972");

        UpdateUserDTO expectedDTO = new UpdateUserDTO();
        expectedDTO.setFirstName("Артём");
        expectedDTO.setLastName("Акопян");
        expectedDTO.setPhone("+79996311972");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toUpdateUserDTO(updatedUser)).thenReturn(expectedDTO);

        UpdateUserDTO result = profileService.updateProfile(userId, updateUserDTO);

        assertThat(result).isEqualTo(expectedDTO);
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toUpdateUserDTO(updatedUser);
    }

    @Test
    void updateProfile_whenUserNotFound_throwsException() {
        Long userId = 1L;
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.updateProfile(userId, updateUserDTO))
                .isInstanceOf(UserNotFoundAuthException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void deleteProfile_whenAdmin_deletesUser() {
        Long userId = 1L;
        String token = "validToken";

        JwtUserInfo jwtUserInfo = new JwtUserInfo();
        jwtUserInfo.setUserId(999L); // админ
        jwtUserInfo.setRole("ADMIN");

        User user = new User();
        user.setId(userId);
        user.setImage("avatar.png");

        when(jwtService.parseToken(token)).thenReturn(jwtUserInfo);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        profileService.deleteProfile(userId, token);

        verify(avatarService).deleteAvatar("avatar.png");
        verify(userRepository).delete(user);
    }

    @Test
    void deleteProfile_whenOwner_deletesOwnProfile() {
        Long userId = 1L;
        String token = "token";

        JwtUserInfo jwtUserInfo = new JwtUserInfo();
        jwtUserInfo.setUserId(userId);
        jwtUserInfo.setRole("USER");

        User user = new User();
        user.setId(userId);
        user.setImage(null);

        when(jwtService.parseToken(token)).thenReturn(jwtUserInfo);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        profileService.deleteProfile(userId, token);

        verify(userRepository).delete(user);
        verify(avatarService, never()).deleteAvatar(any());
    }

    @Test
    void deleteProfile_whenUnauthorizedUser_throwsAccessDenied() {
        Long targetUserId = 2L;
        String token = "token";

        JwtUserInfo jwtUserInfo = new JwtUserInfo();
        jwtUserInfo.setUserId(1L);
        jwtUserInfo.setRole("USER");

        when(jwtService.parseToken(token)).thenReturn(jwtUserInfo);

        assertThatThrownBy(() -> profileService.deleteProfile(targetUserId, token))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("У вас нет прав");
    }

    @Test
    void saveOrUpdateAvatar_whenUserExistsAndHasOldAvatar_deletesOldAndSavesNew() throws IOException {
        Long userId = 1L;
        MultipartFile file = mock(MultipartFile.class);

        User user = new User();
        user.setId(userId);
        user.setImage(userId + "/old-avatar.png");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(avatarService.saveAvatar(file, userId)).thenReturn(userId+ "/new-avatar.png");

        String result = profileService.saveOrUpdateAvatar(userId, file);

        assertThat(result).isEqualTo(userId + "/new-avatar.png");
        verify(avatarService).deleteAvatar(userId + "/old-avatar.png");
        verify(avatarService).saveAvatar(file, userId);
        verify(userRepository).save(user);
    }

    @Test
    void saveOrUpdateAvatar_whenUserNotFound_throwsException() {
        Long userId = 1L;
        MultipartFile file = mock(MultipartFile.class);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.saveOrUpdateAvatar(userId, file))
                .isInstanceOf(UserNotFoundAuthException.class)
                .hasMessageContaining("User not found");
    }
}

