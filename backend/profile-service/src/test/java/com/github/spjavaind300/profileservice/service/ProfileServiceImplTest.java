package com.github.spjavaind300.profileservice.service;

import com.github.spjavaind300.profileservice.dto.JwtUserInfo;
import com.github.spjavaind300.profileservice.dto.UpdateUserDTO;
import com.github.spjavaind300.profileservice.dto.UserDTO;
import com.github.spjavaind300.profileservice.exception.UserAuthException;
import com.github.spjavaind300.profileservice.mapper.UserMapper;
import com.github.spjavaind300.profileservice.model.entity.User;
import com.github.spjavaind300.profileservice.repository.UserRepository;
import com.github.spjavaind300.profileservice.service.impl.AvatarServiceImpl;
import com.github.spjavaind300.profileservice.service.impl.JwtServiceImpl;
import com.github.spjavaind300.profileservice.service.impl.ProfileServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.getProfile(userId))
                .isInstanceOf(UserAuthException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void updateProfile_whenUserExists_updatesAndReturnsUpdateUserDTO() {
        long userId = 1L;

        UpdateUserDTO inputDTO = new UpdateUserDTO();
        inputDTO.setFirstName("Артём");
        inputDTO.setLastName("Акопян");
        inputDTO.setPhone("+79996311972");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setFirstName("Старое имя");
        existingUser.setLastName("Старая фамилия");
        existingUser.setPhone("+79990000000");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setFirstName("Артём");
        updatedUser.setLastName("Акопян");
        updatedUser.setPhone("+79996311972");

        UpdateUserDTO expectedDTO = new UpdateUserDTO();
        expectedDTO.setFirstName("Артём");
        expectedDTO.setLastName("Акопян");
        expectedDTO.setPhone("+79996311972");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        doAnswer(invocation -> {
            UpdateUserDTO dto = invocation.getArgument(0);
            User user = invocation.getArgument(1);
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setPhone(dto.getPhone());
            return null;
        }).when(userMapper).toUpdatedUserEntity(any(UpdateUserDTO.class), any(User.class));

        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(userMapper.toUpdateUserDTO(updatedUser)).thenReturn(expectedDTO);

        UpdateUserDTO result = profileService.updateProfile(userId, inputDTO);

        assertThat(result).isEqualTo(expectedDTO);

        verify(userRepository).findById(userId);
        verify(userMapper).toUpdatedUserEntity(inputDTO, existingUser); // важно!
        verify(userRepository).save(existingUser);
        verify(userMapper).toUpdateUserDTO(updatedUser);
    }


    @Test
    void updateProfile_whenUserNotFound_throwsException() {
        long userId = 1L;
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.updateProfile(userId, updateUserDTO))
                .isInstanceOf(UserAuthException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void updateProfile_whenDtoHasNullFields_shouldNotOverwriteWithNull() {
        long userId = 1L;

        UpdateUserDTO updateDto = new UpdateUserDTO();
        updateDto.setFirstName("Новое имя");
        updateDto.setLastName(null);
        updateDto.setPhone(null);

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setFirstName("Старое имя");
        existingUser.setLastName("Старая фамилия");
        existingUser.setPhone("+79998887766");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setFirstName("Новое имя");
        updatedUser.setLastName("Старая фамилия");
        updatedUser.setPhone("+79998887766");

        UpdateUserDTO expectedDto = new UpdateUserDTO();
        expectedDto.setFirstName("Новое имя");
        expectedDto.setLastName("Старая фамилия");
        expectedDto.setPhone("+79998887766");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        doAnswer(invocation -> {
            UpdateUserDTO dto = invocation.getArgument(0);
            User user = invocation.getArgument(1);

            if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
            if (dto.getLastName() != null) user.setLastName(dto.getLastName());
            if (dto.getPhone() != null) user.setPhone(dto.getPhone());

            return null;
        }).when(userMapper).toUpdatedUserEntity(updateDto, existingUser);

        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(userMapper.toUpdateUserDTO(updatedUser)).thenReturn(expectedDto);

        UpdateUserDTO result = profileService.updateProfile(userId, updateDto);

        assertThat(result).isEqualTo(expectedDto);
        assertThat(existingUser.getFirstName()).isEqualTo("Новое имя");
        assertThat(existingUser.getLastName()).isEqualTo("Старая фамилия");
        assertThat(existingUser.getPhone()).isEqualTo("+79998887766");

        verify(userRepository).findById(userId);
        verify(userMapper).toUpdatedUserEntity(updateDto, existingUser);
        verify(userRepository).save(existingUser);
        verify(userMapper).toUpdateUserDTO(updatedUser);
    }

    @Test
    void deleteProfile_whenAdmin_deletesUser() {
        long userId = 1L;
        String token = "validToken";

        JwtUserInfo jwtUserInfo = new JwtUserInfo();
        jwtUserInfo.setUserId(10L);
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
        long userId = 1L;
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
        long targetUserId = 2L;
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
    void updateAvatar_whenUserExistsAndHasOldAvatar_deletesOldAndSavesNew() throws IOException {
        long userId = 1L;
        MultipartFile file = mock(MultipartFile.class);

        User user = new User();
        user.setId(userId);
        user.setImage(userId + "/old-avatar.png");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(avatarService.saveAvatar(file, userId)).thenReturn(userId + "/new-avatar.png");

        String result = profileService.updateAvatar(userId, file);

        assertThat(result).isEqualTo(userId + "/new-avatar.png");
        verify(avatarService).deleteAvatar(userId + "/old-avatar.png");
        verify(avatarService).saveAvatar(file, userId);
        verify(userRepository).save(user);
    }

    @Test
    void updateAvatar_whenUserNotFound_throwsException() {
        long userId = 1L;
        MultipartFile file = mock(MultipartFile.class);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.updateAvatar(userId, file))
                .isInstanceOf(UserAuthException.class)
                .hasMessageContaining("User not found");
    }
}

