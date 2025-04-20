package com.github.spjavaind300.profileservice.service;

import com.github.spjavaind300.profileservice.dto.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Listener for user creation events.
 * <p>
 * Subscribes to the Kafka topic "auth.user.created" and delegates
 * the creation of a user profile to {@link ProfileService#createProfile(UserCreatedEvent)}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserCreatedListener {

    private final ProfileService profileService;

    /**
     * Handles a new user creation event.
     * <p>
     * Upon receiving a {@code UserCreatedEvent}, logs the event
     * and invokes the {@code ProfileService} to create the corresponding profile.
     *
     * @param event the event containing information about the newly created user,
     *              such as their identifier and initial profile data
     * @throws NullPointerException if {@code event} is {@code null}
     * @see ProfileService#createProfile(UserCreatedEvent)
     */
    @KafkaListener(
            topics  = "auth.user.created",
            groupId = "profile-service-consumer"
    )
    public void handleUserCreated(UserCreatedEvent event) {
        log.info("Received user creation event: {}", event);
        profileService.createProfile(event);
    }
}

