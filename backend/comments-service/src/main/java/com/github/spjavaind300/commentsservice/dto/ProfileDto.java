package com.github.spjavaind300.commentsservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProfileDto {

    @NotNull
    @JsonProperty("id")
    private Long authorId;

    @NotBlank
    @JsonProperty("firstName")
    private String authorFirstName;

    @NotBlank
    @JsonProperty("image")
    private String authorImage;
}