package com.github.spjavaind300.commentsservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CommentDto {

    private Long pk;

    private String text;

    private Instant createdAt;

    private ProfileDto authorId;

    private ProfileDto authorFirstName;

    private ProfileDto authorImage;
}
