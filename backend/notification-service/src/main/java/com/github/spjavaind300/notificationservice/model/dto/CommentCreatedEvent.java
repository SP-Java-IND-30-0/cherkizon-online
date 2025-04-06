package com.github.spjavaind300.notificationservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.spjavaind300.notificationservice.model.NotificationType;

public record CommentCreatedEvent(
        @JsonProperty("id") long id,
        @JsonProperty("adv_id") long advId,
        @JsonProperty("author_first_name") String firstName,
        @JsonProperty("author_last_name") String lastName,
        @JsonProperty("comment_uri") String commentUri
) implements Event{

    public static final String SUBJECT = "Новый комментарий!";

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.COMMENT_CREATED;
    }

    @Override
    public String getSubject() {
        return SUBJECT;
    }
}
