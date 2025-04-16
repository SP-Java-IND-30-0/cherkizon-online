package com.github.spjavaind300.notificationservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.spjavaind300.notificationservice.model.NotificationType;

public record AdvUpdatedEvent(
        @JsonProperty("id") int id,
        @JsonProperty("title") String title,
        @JsonProperty("author_first_name") String firstName,
        @JsonProperty("author_last_name") String lastName,
        @JsonProperty("adv_uri") String advUri

) implements Event {

    private static final String SUBJECT = "Обновлено объявление!";

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.ADV_UPDATED;
    }

    @Override
    public String getSubject() {
        return SUBJECT;
    }
}
