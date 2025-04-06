package com.github.spjavaind300.commentsservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class CommentDto {

    @JsonProperty("pk")
    private long id;

    private String text;

    private Instant createdAt;

    private long author;

    private String authorFirstName;

    private String authorImage;
}