package com.github.spjavaind300.commentsservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserDto {

    @Schema(description = "ID автора", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Имя автора")
    private String authorFirstName;

    @Schema(description = "Аватар автора")
    private String authorImage;
}