package com.github.spjavaind300.commentsservice.kafka;

import com.github.spjavaind300.commentsservice.kafka.dto.AdDeletedEvent;
import com.github.spjavaind300.commentsservice.kafka.dto.UserDeletedEvent;
import com.github.spjavaind300.commentsservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentKafkaListener {

    private final CommentService commentService;

    @KafkaListener(
            topics = "profile.user.deleted",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "userDeletedKafkaListenerContainerFactory"
    )
    public void handleUserDeleted(UserDeletedEvent event) {
        log.info("Received user deleted event: {}", event);
        commentService.deleteCommentsByAuthorId(event.getId());
    }

    @KafkaListener(
            topics = "adv.deleted",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "adDeletedKafkaListenerContainerFactory"
    )
    public void handleAdDeleted(AdDeletedEvent event) {
        log.info("Received ad deleted event: {}", event);
        commentService.deleteCommentsByAdId(event.getId());
    }
}