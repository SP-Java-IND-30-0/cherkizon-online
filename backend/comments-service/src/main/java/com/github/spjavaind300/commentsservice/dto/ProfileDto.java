package com.github.spjavaind300.commentsservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProfileDto {

    @NotNull
    private Long authorId;

    @NotBlank
    private String authorFirstName;

    @NotBlank
    private String authorImage;
}