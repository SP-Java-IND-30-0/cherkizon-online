package com.github.spjavaind300.profileservice.service;

import com.github.spjavaind300.profileservice.dto.event.UserDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileKafkaProducerService {

    private final KafkaTemplate<String, UserDeletedEvent> kafkaTemplate;

    @Retryable(
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = KafkaException.class
    )
    public void publishUserDeleted(UserDeletedEvent event) {
        kafkaTemplate.send(
                        "profile.user.deleted",
                        String.valueOf(event.userId()),
                        event
                )
                .thenApply(r -> {
                    log.info("UserDeletedEvent {} published", event);
                    return null;
                })
                .exceptionally(ex -> {
                    log.error("Failed to send UserDeletedEvent {}: {}", event, ex.getMessage());
                    throw new KafkaException("Publish failed", ex);
                });
    }

    @Recover
    public CompletableFuture<Void> recover (UserDeletedEvent event, Throwable cause) {
        log.error("All retries failed for UserDeletedEvent {}: {}", event, cause.getMessage());
        return CompletableFuture.failedFuture(cause);
    }
}