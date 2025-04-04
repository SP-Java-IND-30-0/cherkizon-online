package com.github.spjavaind300.notificationservice.model.dto;

import com.github.spjavaind300.notificationservice.model.NotificationType;

public interface Event {
    NotificationType getNotificationType();
    String getSubject();
}
