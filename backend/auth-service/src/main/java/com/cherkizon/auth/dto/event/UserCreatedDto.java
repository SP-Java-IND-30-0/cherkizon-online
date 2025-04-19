package com.cherkizon.auth.dto.event;

public record UserCreatedDto(
        long id,
        String username,
        String firstname,
        String lastname,
        String phone
) implements UserEvent {
    public static final String EVENT_TOPIC = "auth.user.created";


    @Override
    public String eventId() {
        return Long.toString(id);
    }

    @Override
    public String topic() {
        return EVENT_TOPIC;
    }

    @Override
    public String toString() {
        return String.format("Event: %s, id: %d", EVENT_TOPIC, id);
    }
}
