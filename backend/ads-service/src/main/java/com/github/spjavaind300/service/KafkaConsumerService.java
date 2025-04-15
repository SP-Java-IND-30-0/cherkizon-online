package com.github.spjavaind300.service;

import com.github.spjavaind300.model.UserDeletedEvent;
import com.github.spjavaind300.model.dto.Role;
import com.github.spjavaind300.model.dto.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final AdService adService;

    @KafkaListener(topics = "profile.user.deleted", groupId = "ads-group")
    public void handleEvent(UserDeletedEvent event) {

        adService.deleteAllByUserId(event.id(), new UserContext(event.id(), Role.SERVICE));
    }
}
