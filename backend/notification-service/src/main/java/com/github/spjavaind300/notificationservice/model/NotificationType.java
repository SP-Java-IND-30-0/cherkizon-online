package com.github.spjavaind300.notificationservice.model;

import lombok.Getter;

@Getter
public enum NotificationType {
    USER_CREATED ("greeting-letter.html"),
    ADV_UPDATED ("adv-updated.html"),
    COMMENT_CREATED ("comment-created.html");

    private final String templateName;

    NotificationType(String templateName) {
        this.templateName = templateName;
    }

}
