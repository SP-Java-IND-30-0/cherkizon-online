package com.github.spjavaind300.profileservice.service.impl;

import com.github.spjavaind300.profileservice.dto.JwtUserInfo;
import com.github.spjavaind300.profileservice.mapper.UserMapper;
import com.github.spjavaind300.profileservice.dto.UpdateUserDTO;
import com.github.spjavaind300.profileservice.dto.UserDTO;
import com.github.spjavaind300.profileservice.exception.UserNotFoundAuthException;
import com.github.spjavaind300.profileservice.model.entity.User;
import com.github.spjavaind300.profileservice.repository.UserRepository;
import com.github.spjavaind300.profileservice.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final AvatarServiceImpl avatarService;
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final JwtServiceImpl jwtService;

    @Autowired
    public ProfileServiceImpl(AvatarServiceImpl avatarService,
                              UserRepository userRepository, UserMapper userMapper,
                              JwtServiceImpl jwtService) {
        this.avatarService = avatarService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
    }

    @Override
    public UserDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundAuthException("Пользователь не найден: " + userId));
        return userMapper.toUserDTO(user);
    }

    @Override
    public UpdateUserDTO updateProfile(Long userId, UpdateUserDTO updatedData) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundAuthException("Пользователь не найден: " + userId));

        user.setFirstName(updatedData.getFirstName());
        user.setLastName(updatedData.getLastName());
        user.setPhone(updatedData.getPhone());

        User updatedUser = userRepository.save(user);

        return userMapper.toUpdateUserDTO(updatedUser);
    }

    @Override
    public void deleteProfile(Long targetUserId, String token) {
        JwtUserInfo jwtUser = jwtService.parseToken(token);
        Long requesterId = jwtUser.getUserId();
        String role = jwtUser.getRole();

        if (!role.equals("ADMIN") && !requesterId.equals(targetUserId)) {
            throw new AccessDeniedException("У вас нет прав на удаление аккаунта");
        }
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundAuthException("User not found: " + targetUserId));
        if (user.getImage() != null) {
            avatarService.deleteAvatar(user.getImage());
        }
        userRepository.delete(user);
    }

    public String saveOrUpdateAvatar(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundAuthException("User not found"));

        if (user.getImage() != null) {
            avatarService.deleteAvatar(user.getImage());
        }
        String avatarUrl = avatarService.saveAvatar(file, userId);
        user.setImage(avatarUrl);
        userRepository.save(user);
        return avatarUrl;
    }
}
