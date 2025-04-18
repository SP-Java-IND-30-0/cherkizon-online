package com.cherkizon.auth.service;

import com.cherkizon.auth.dto.event.UserEvent;
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
public class KafkaProducerService implements EventPublisher {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Retryable(maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = {KafkaException.class})
    @Override
    public void publish(UserEvent event) {
        kafkaTemplate.send(event.topic(), event.eventId(), event)
                .thenApply(result -> {
                    log.info("User event {} successfully published", event);
                    return null;
                })
                .exceptionally(ex -> {
                    log.error("Kafka send failed for event {}. Error: {}", event, ex.getMessage());
                    throw new KafkaException("Kafka send failed", ex);
                });
    }

    @Recover
    public CompletableFuture<Void> publishFallback(UserEvent event, Throwable t) {
        log.error("All retries failed for event {}. Error: {}", event.eventId(), t.getMessage());
        return CompletableFuture.failedFuture(t);
    }
}
