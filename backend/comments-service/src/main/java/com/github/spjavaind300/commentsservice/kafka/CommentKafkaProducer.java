package com.github.spjavaind300.commentsservice.kafka;

import com.github.spjavaind300.commentsservice.kafka.dto.CommentCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentKafkaProducer {

    private final KafkaTemplate<String, CommentCreatedEvent> kafkaTemplate;

    public void sendCommentCreatedEvent(CommentCreatedEvent event) {
        String topic = "comment.created";

        CompletableFuture<SendResult<String, CommentCreatedEvent>> future =
                kafkaTemplate.send(topic, String.valueOf(event.getCommentId()), event);

        future.thenAccept(result -> {
            log.info("Sent comment created event: {}", event);
        });

        future.exceptionally(ex -> {
            log.error("Failed to send comment created event: {}", event, ex);
            return null;
        });
    }
}