package com.github.spjavaind300.commentsservice.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class CommentDto {

    private Long pk;

    private String text;

    private Instant createdAt;

    private ProfileDto authorId;

    private ProfileDto authorFirstName;

    private ProfileDto authorImage;
}