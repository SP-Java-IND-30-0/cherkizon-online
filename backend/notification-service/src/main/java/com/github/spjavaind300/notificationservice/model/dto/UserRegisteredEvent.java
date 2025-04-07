package com.github.spjavaind300.notificationservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.spjavaind300.notificationservice.model.NotificationType;

public record UserRegisteredEvent
        (@JsonProperty("id") long id,
         @JsonProperty("username") String email,
         @JsonProperty("first_name") String firstName,
         @JsonProperty("last_name") String lastName,
         @JsonProperty("phone") String phone)
implements Event{

    private static final String SUBJECT = "Поздравляем с регистрацией!";

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.USER_CREATED;
    }

    @Override
    public String getSubject() {
        return SUBJECT;
    }
}
