package com.github.spjavaind300.notificationservice.service;

import com.github.spjavaind300.notificationservice.model.dto.EmailDto;
import com.github.spjavaind300.notificationservice.model.dto.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final EmailService emailService;
    private final EmailStrategyFactory factory;

    @KafkaListener(topics = {
            "auth.user.created",
            "adv.updated",
            "comment.created"},
            autoStartup = "${kafka.listener.auto-startup:false}")
    public void handleEvent(Event event) {
        NotificationStrategy<Event> strategy = factory.getStrategy(event.getNotificationType());
        for (EmailDto emailDto : strategy.prepareEmail(event)) {
            emailService.sendEmailAsync(emailDto);
        }
    }
}
