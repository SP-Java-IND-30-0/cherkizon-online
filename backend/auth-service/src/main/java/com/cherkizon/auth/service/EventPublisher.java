package com.cherkizon.auth.service;

import com.cherkizon.auth.dto.event.UserEvent;

public interface EventPublisher {

    void publish(UserEvent event);
}
