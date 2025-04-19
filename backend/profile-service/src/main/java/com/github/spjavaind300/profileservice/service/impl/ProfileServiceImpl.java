package com.github.spjavaind300.profileservice.service.impl;

import com.github.spjavaind300.profileservice.dto.Role;
import com.github.spjavaind300.profileservice.dto.event.UserCreatedEvent;
import com.github.spjavaind300.profileservice.dto.event.UserDeletedEvent;
import com.github.spjavaind300.profileservice.exception.AccessDeniedProfileException;
import com.github.spjavaind300.profileservice.mapper.UserMapper;
import com.github.spjavaind300.profileservice.dto.UpdateUserDTO;
import com.github.spjavaind300.profileservice.dto.UserDTO;
import com.github.spjavaind300.profileservice.exception.UserNotFoundException;
import com.github.spjavaind300.profileservice.model.entity.User;
import com.github.spjavaind300.profileservice.repository.UserRepository;
import com.github.spjavaind300.profileservice.security.CustomUserDetails;
import com.github.spjavaind300.profileservice.service.AvatarService;
import com.github.spjavaind300.profileservice.service.ProfileKafkaProducerService;
import com.github.spjavaind300.profileservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private final AvatarService avatarService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ProfileKafkaProducerService kafkaProducer;


    @Override
    public UserDTO getProfile(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + userId));
        return userMapper.toUserDTO(user);
    }

    @Transactional
    @Override
    public UpdateUserDTO updateProfile(long userId, UpdateUserDTO updatedData) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + userId));

        userMapper.toUpdatedUserEntity(updatedData, user);

        User updatedUser = userRepository.save(user);

        return userMapper.toUpdateUserDTO(updatedUser);
    }

    @Transactional
    @Override
    public void deleteProfile(long targetUserId) {
        User user = userRepository.findById(targetUserId).orElseThrow(()
                -> new UserNotFoundException("Пользователь не найден: " + targetUserId));
        checkUserAccess(user.getId());
        if (user.getImage() != null) {
            avatarService.deleteAvatar(user.getImage());
        }
        userRepository.delete(user);
        kafkaProducer.publishUserDeleted(new UserDeletedEvent(user.getId()));
    }

    @Transactional
    public String updateAvatar(long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + userId));

        if (user.getImage() != null) {
            avatarService.deleteAvatar(user.getImage());
        }
        String avatarUrl = avatarService.saveAvatar(file, userId);
        user.setImage(avatarUrl);
        userRepository.save(user);
        return avatarUrl;
    }

    private void checkUserAccess(long userId) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        if (userDetails.role() == Role.USER && userDetails.userId() != userId) {
            throw new AccessDeniedProfileException();
        }
    }

    @Override
    @Transactional
    public void createProfile(UserCreatedEvent event) {
        User user = new User();
        user.setId(event.id());
        user.setEmail(event.email());
        user.setFirstName(event.firstName());
        user.setLastName(event.lastName());
        user.setPhone(event.phone());
        userRepository.save(user);
    }
}