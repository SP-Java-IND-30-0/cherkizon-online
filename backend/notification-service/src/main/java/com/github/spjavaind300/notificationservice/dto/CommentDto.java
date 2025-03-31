package com.github.spjavaind300.notificationservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {

    private LocalDateTime createdAt;
    private String authorName;
    private String commentText;
    private UserDto recipient;
    private String advTitle;

}
