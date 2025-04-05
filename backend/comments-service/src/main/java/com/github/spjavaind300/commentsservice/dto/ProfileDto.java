package com.github.spjavaind300.commentsservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProfileDto {

    @NotNull(message = "Author ID cannot be null")
    private Integer authorId;

    @NotBlank(message = "Author's first name cannot be blank")
    private String authorFirstName;

    @NotBlank(message = "Author's image cannot be blank")
    private String authorImage;
}