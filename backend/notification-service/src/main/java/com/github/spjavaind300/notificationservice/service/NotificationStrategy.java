package com.github.spjavaind300.notificationservice.service;

import com.github.spjavaind300.notificationservice.model.NotificationType;
import com.github.spjavaind300.notificationservice.model.dto.EmailDto;
import com.github.spjavaind300.notificationservice.model.dto.Event;

import java.util.List;

public interface NotificationStrategy<T extends Event> {

    NotificationType getType();

    List<EmailDto> prepareEmail(T event);


}
