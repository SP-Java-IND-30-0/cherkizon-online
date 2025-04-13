package com.github.spjavaind300.model.event;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AdUpdatedEvent(
        int id,
        String title,
        @JsonProperty("author_first_name") String authorFirstName,
        @JsonProperty("author_last_name") String authorLastName,
        @JsonProperty("adv_uri") String advUri
) implements AdEvent {

    private static final String TOPIC = "adv.updated";

    @Override
    public String eventId() {
        return Integer.toString(id);
    }

    @Override
    public String topic() {
        return TOPIC;
    }

}
