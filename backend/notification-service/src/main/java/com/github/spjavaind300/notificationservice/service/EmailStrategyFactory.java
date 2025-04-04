package com.github.spjavaind300.notificationservice.service;

import com.github.spjavaind300.notificationservice.model.NotificationType;
import com.github.spjavaind300.notificationservice.model.dto.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailStrategyFactory {

    private final List<NotificationStrategy<Event>> strategies;

    public NotificationStrategy<Event> getStrategy(NotificationType type) {
        return strategies.stream()
                .filter(strategy -> strategy.getType() == type)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No strategy found for " + type)); //TODO: throw custom exception
    }
}
