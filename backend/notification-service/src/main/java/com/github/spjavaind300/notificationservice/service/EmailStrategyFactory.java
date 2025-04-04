package com.github.spjavaind300.notificationservice.service;

import com.github.spjavaind300.notificationservice.model.NotificationType;
import com.github.spjavaind300.notificationservice.model.dto.Event;
import jakarta.el.MethodNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EmailStrategyFactory {


    private final Map<NotificationType, NotificationStrategy<Event>> strategyMap = new HashMap<>();

    EmailStrategyFactory(@Autowired List<NotificationStrategy<Event>> strategies) {
        for (NotificationStrategy<Event> strategy : strategies) {
            strategyMap.put(strategy.getType(), strategy);
        }
    }

    public NotificationStrategy<Event> getStrategy(NotificationType type) {

        return Optional.ofNullable(strategyMap.get(type))
                .orElseThrow(() -> new MethodNotFoundException("No email prepare strategy found for type " + type));
    }
}
