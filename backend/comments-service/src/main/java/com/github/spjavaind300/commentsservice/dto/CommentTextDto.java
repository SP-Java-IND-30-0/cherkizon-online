package com.github.spjavaind300.commentsservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentTextDto {

    @NotBlank
    private String text;
}