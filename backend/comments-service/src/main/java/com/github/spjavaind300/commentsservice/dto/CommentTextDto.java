package com.github.spjavaind300.commentsservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object used for creating or updating the textual content of a comment.
 */
@Data
public class CommentTextDto {

    /**
     * The text content of the comment.
     * Must not be blank.
     */
    @NotBlank
    private String text;
}