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

/**
 * Implementation of {@link ProfileService} for managing user profiles.
 * <p>
 * Performs operations such as retrieving, updating, and deleting user profiles,
 * handling avatar uploads, and creating profiles on user lifecycle events.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private final AvatarService avatarService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final InternalUserMapper internalUserMapper;
    private final ProfileKafkaProducerService kafkaProducer;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDTO getProfile(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        return userMapper.toUserDTO(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternalUserResponse getUserForAdsRequest(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        return internalUserMapper.toResponse(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternalProfileResponse getProfileForAdsRequest(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        return internalUserMapper.toProfile(user);
    }

    /**
     * {@inheritDoc}
     */
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
            throw new UserNotFoundException("Users not found: " + missing);
        }
        return users.stream()
                .map(internalUserMapper::toSummary)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UpdateUserDTO updateProfile(long userId, UpdateUserDTO updatedData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        userMapper.toUpdatedUserEntity(updatedData, user);
        User updatedUser = userRepository.save(user);
        return userMapper.toUpdateUserDTO(updatedUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteProfile(long targetUserId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + targetUserId));
        checkUserAccess(user.getId());
        if (user.getImage() != null) {
            avatarService.deleteAvatar(user.getImage());
        }
        userRepository.delete(user);
        kafkaProducer.publishUserDeleted(new UserDeletedEvent(user.getId()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public String updateAvatar(long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        if (user.getImage() != null) {
            avatarService.deleteAvatar(user.getImage());
        }
        String avatarUrl = avatarService.saveAvatar(file, userId);
        user.setImage(avatarUrl);
        userRepository.save(user);
        return avatarUrl;
    }

    /**
     * {@inheritDoc}
     */
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

    private void checkUserAccess(long userId) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        if (userDetails.role() == Role.USER && userDetails.userId() != userId) {
            throw new AccessDeniedProfileException();
        }
    }
}