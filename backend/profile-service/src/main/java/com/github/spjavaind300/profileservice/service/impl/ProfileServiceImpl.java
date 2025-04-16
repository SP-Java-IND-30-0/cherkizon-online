package com.github.spjavaind300.profileservice.service.impl;

import com.github.spjavaind300.profileservice.dto.JwtUserInfo;
import com.github.spjavaind300.profileservice.exception.AccessDeniedProfileException;
import com.github.spjavaind300.profileservice.mapper.UserMapper;
import com.github.spjavaind300.profileservice.dto.UpdateUserDTO;
import com.github.spjavaind300.profileservice.dto.UserDTO;
import com.github.spjavaind300.profileservice.exception.UserAuthException;
import com.github.spjavaind300.profileservice.model.entity.User;
import com.github.spjavaind300.profileservice.repository.UserRepository;
import com.github.spjavaind300.profileservice.service.AvatarService;
import com.github.spjavaind300.profileservice.service.JwtService;
import com.github.spjavaind300.profileservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final AvatarService avatarService;
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final JwtService jwtService;

    @Override
    public UserDTO getProfile(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserAuthException("Пользователь не найден: " + userId));
        return userMapper.toUserDTO(user);
    }

    @Override
    public UpdateUserDTO updateProfile(long userId, UpdateUserDTO updatedData) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserAuthException("Пользователь не найден: " + userId));

        userMapper.toUpdatedUserEntity(updatedData, user);

        User updatedUser = userRepository.save(user);

        return userMapper.toUpdateUserDTO(updatedUser);
    }

    @Override
    public void deleteProfile(long targetUserId, String token) {
        JwtUserInfo jwtUser = jwtService.parseToken(token);
        Long requesterId = jwtUser.getUserId();
        String role = jwtUser.getRole();

        if (!role.equals("ADMIN") && !requesterId.equals(targetUserId)) {
            throw new AccessDeniedProfileException("У вас нет прав на удаление аккаунта");
        }
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserAuthException("User not found: " + targetUserId));
        if (user.getImage() != null) {
            avatarService.deleteAvatar(user.getImage());
        }
        userRepository.delete(user);
    }

    public String updateAvatar(long userId, MultipartFile file){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserAuthException("User not found"));

        if (user.getImage() != null) {
            avatarService.deleteAvatar(user.getImage());
        }
        String avatarUrl = avatarService.saveAvatar(file, userId);
        user.setImage(avatarUrl);
        userRepository.save(user);
        return avatarUrl;
    }
}
