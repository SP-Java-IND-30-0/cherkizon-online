package com.github.spjavaind300.notificationservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.spjavaind300.notificationservice.model.NotificationType;

public record CommentCreated(
        @JsonProperty("id") int id,
        @JsonProperty("adv_id") int advId,
        @JsonProperty("author_first_name") String firstName,
        @JsonProperty("author_last_name") String lastName,
        @JsonProperty("comment_uri") String commentUri
) implements Event{
    @Override
    public NotificationType getNotificationType() {
        return NotificationType.COMMENT_CREATED;
    }
}
