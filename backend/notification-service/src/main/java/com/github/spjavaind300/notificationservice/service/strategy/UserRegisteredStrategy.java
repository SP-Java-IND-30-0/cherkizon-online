package com.github.spjavaind300.notificationservice.service.strategy;

import com.github.spjavaind300.notificationservice.model.NotificationType;
import com.github.spjavaind300.notificationservice.model.dto.EmailDto;
import com.github.spjavaind300.notificationservice.model.dto.UserRegisteredEvent;
import com.github.spjavaind300.notificationservice.service.NotificationStrategy;
import com.github.spjavaind300.notificationservice.utils.EmailTemplateCache;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRegisteredStrategy implements NotificationStrategy<UserRegisteredEvent> {

    private final String caption;

    private final String supportLink;

    private final EmailTemplateCache templateCache;

    public UserRegisteredStrategy(
            @Qualifier("caption") String caption,
            @Qualifier("supportLink") String supportLink,
            EmailTemplateCache templateCache) {
        this.caption = caption;
        this.supportLink = supportLink;
        this.templateCache = templateCache;
    }

    @Override
    public NotificationType getType() {
        return NotificationType.USER_CREATED;
    }

    @Override
    public List<EmailDto> prepareEmail(UserRegisteredEvent event) {
        return List.of(
                EmailDto.builder()
                        .email(event.email())
                        .subject(event.getSubject())
                        .body(String.format(
                                templateCache.getTemplate(getType()),
                                event.getSubject(),
                                event.firstName(),
                                supportLink,
                                caption))
                        .build()
        );
    }
}
