package com.github.spjavaind300.notificationservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.spjavaind300.notificationservice.model.NotificationType;

public record AdvUpdated(
        @JsonProperty("id") int id,
        @JsonProperty("title") String title,
        @JsonProperty("author_first_name") String firstName,
        @JsonProperty("author_last_name") String lastName,
        @JsonProperty("adv_uri") String advUri

) implements Event {
    @Override
    public NotificationType getNotificationType() {
        return NotificationType.ADV_UPDATED;
    }
}
