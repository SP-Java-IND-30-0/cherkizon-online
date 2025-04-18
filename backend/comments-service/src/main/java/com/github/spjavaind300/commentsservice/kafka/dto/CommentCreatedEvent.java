package com.github.spjavaind300.commentsservice.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreatedEvent {
    private int commentId;
    private int adId;
    private long authorId;
    private String text;
    private Instant createdAt;
}