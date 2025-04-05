package com.github.spjavaind300.commentsservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCommentDto {

    @NotBlank(message = "Text cannot be empty")
    private String text;
}