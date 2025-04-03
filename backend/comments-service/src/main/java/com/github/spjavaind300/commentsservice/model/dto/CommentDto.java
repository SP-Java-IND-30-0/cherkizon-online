package com.github.spjavaind300.commentsservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CommentDto {

    @Schema(description = "ID комментария", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Текст комментария")
    private String text;

    @Schema(description = "Дата и время создания комментария")
    private Long createdAt;

    @Schema(description = "Информация об авторе комментария")
    private UserDto author;
}