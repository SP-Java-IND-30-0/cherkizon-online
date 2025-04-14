package com.github.spjavaind300.service;

import com.github.spjavaind300.model.event.AdEvent;

public interface OutboxEventService {

    void saveOutboxEvent(AdEvent event);

}
