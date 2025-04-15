package com.github.spjavaind300.commentsservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

@Data
public class CommentDto {

    @JsonProperty("pk")
    private int id;

    @Size(min = 1, max = 255, message = "Text length should be in the range from 1 to 255 characters")
    private String text;

    private Instant createdAt;

    @NotNull
    @JsonProperty("author")
    private long authorId;

    private String authorFirstName;

    private String authorImage;
}