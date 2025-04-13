package com.github.spjavaind300.model.event;

public record AdDeletedEvent(int id) implements AdEvent {
    private static final String TOPIC = "adv.deleted";

    @Override
    public String eventId() {
        return Integer.toString(id);
    }

    @Override
    public String topic() {
        return TOPIC;
    }

}
