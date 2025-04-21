package com.github.spjavaind300.commentsservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

/**
 * Data Transfer Object representing a comment with additional author information.
 * Used for transferring comment data between layers and to clients.
 */
@Data
public class CommentDto {

    /**
     * Unique identifier of the comment.
     * Mapped to JSON property "pk".
     */
    @JsonProperty("pk")
    private int id;

    /**
     * The textual content of the comment.
     * Must be between 1 and 255 characters.
     */
    @Size(min = 1, max = 255, message = "Text length should be in the range from 1 to 255 characters")
    private String text;

    /**
     * The timestamp when the comment was created.
     */
    private Instant createdAt;

    /**
     * ID of the author who created the comment.
     * Mapped to JSON property "author".
     */
    @NotNull
    @JsonProperty("author")
    private long authorId;

    /**
     * First name of the comment's author.
     */
    private String authorFirstName;

    /**
     * URL or identifier of the author's profile image.
     */
    private String authorImage;
}