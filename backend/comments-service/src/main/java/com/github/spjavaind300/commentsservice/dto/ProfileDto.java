package com.github.spjavaind300.commentsservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object representing a user's profile information.
 * Typically used to enrich other DTOs (e.g., comments) with author-related data.
 */
@Data
public class ProfileDto {

    /**
     * Unique identifier of the author.
     * Mapped to JSON property "id".
     */
    @NotNull
    @JsonProperty("id")
    private long authorId;

    /**
     * First name of the author.
     * Mapped to JSON property "firstName".
     */
    @NotBlank
    @JsonProperty("firstName")
    private String authorFirstName;

    /**
     * Profile image of the author.
     * Mapped to JSON property "image".
     */
    @NotBlank
    @JsonProperty("image")
    private String authorImage;
}