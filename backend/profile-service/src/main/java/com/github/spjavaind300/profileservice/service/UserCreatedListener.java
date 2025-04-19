package com.github.spjavaind300.profileservice.service;

import com.github.spjavaind300.profileservice.dto.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCreatedListener {

    private final ProfileService profileService;

    @KafkaListener(
            topics   = "auth.user.created",
            groupId  = "profile-service-consumer"
    )
    public void handleUserCreated(UserCreatedEvent event) {
        log.info("Получено событие создания пользователя: {}", event);
        profileService.createProfile(event);
    }
}
