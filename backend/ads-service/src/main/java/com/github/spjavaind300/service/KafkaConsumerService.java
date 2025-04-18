package com.github.spjavaind300.service;

import com.github.spjavaind300.model.event.UserDeletedEvent;
import com.github.spjavaind300.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final AdService adService;
    private final SecurityService securityService;

    @KafkaListener(topics = "profile.user.deleted", groupId = "ads-group")
    public void handleEvent(UserDeletedEvent event) {

        securityService.serviceAuthentication(event.id());
        adService.deleteAllByUserId(event.id());
    }
}
