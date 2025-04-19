package com.github.spjavaind300.profileservice.service.impl;

import com.github.spjavaind300.profileservice.dto.*;
import com.github.spjavaind300.profileservice.dto.event.UserCreatedEvent;
import com.github.spjavaind300.profileservice.dto.event.UserDeletedEvent;
import com.github.spjavaind300.profileservice.exception.AccessDeniedProfileException;
import com.github.spjavaind300.profileservice.mapper.InternalUserMapper;
import com.github.spjavaind300.profileservice.mapper.UserMapper;
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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private final AvatarService avatarService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final InternalUserMapper internalUserMapper;
    private final ProfileKafkaProducerService kafkaProducer;


    @Override
    public UserDTO getProfile(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + userId));
        return userMapper.toUserDTO(user);
    }

    @Override
    public InternalUserResponse getUserForAdsRequest(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + userId));
        return internalUserMapper.toResponse(user);
    }

    @Override
    public InternalProfileResponse getProfileForAdsRequest(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + userId));
        return internalUserMapper.toProfile(user);
    }

    @Override
    public List<InternalUserSummary> getUserSummariesForAdsRequest(List<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);

        if (users.size() != userIds.size()) {
            Set<Long> foundIds = users.stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());
            List<Long> missing = userIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new UserNotFoundException("Пользователи не найдены: " + missing);
        }
        return users.stream()
                .map(internalUserMapper::toSummary)
                .collect(Collectors.toList());
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