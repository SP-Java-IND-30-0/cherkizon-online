package com.github.spjavaind300.notificationservice.service.strategy;

import com.github.spjavaind300.notificationservice.model.NotificationType;
import com.github.spjavaind300.notificationservice.model.dto.AdvDto;
import com.github.spjavaind300.notificationservice.model.dto.CommentCreatedEvent;
import com.github.spjavaind300.notificationservice.model.dto.EmailDto;
import com.github.spjavaind300.notificationservice.model.dto.UserDto;
import com.github.spjavaind300.notificationservice.service.AdvService;
import com.github.spjavaind300.notificationservice.service.NotificationStrategy;
import com.github.spjavaind300.notificationservice.service.ProfileService;
import com.github.spjavaind300.notificationservice.utils.EmailTemplateCache;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentCreatedStrategy implements NotificationStrategy<CommentCreatedEvent> {

    private final String caption;

    private final String supportLink;

    private final EmailTemplateCache templateCache;

    private final AdvService advService;
    private final ProfileService profileService;

    public CommentCreatedStrategy(
            @Qualifier("caption") String caption,
            @Qualifier("supportLink") String supportLink,
            EmailTemplateCache templateCache, AdvService advService, ProfileService profileService) {
        this.caption = caption;
        this.supportLink = supportLink;
        this.templateCache = templateCache;
        this.advService = advService;
        this.profileService = profileService;
    }


    @Override
    public NotificationType getType() {
        return NotificationType.COMMENT_CREATED;
    }

    @Override
    public List<EmailDto> prepareEmail(CommentCreatedEvent event) {
        AdvDto adv = advService.getAdv(event.advId());
        UserDto user = profileService.getProfiles(List.of(adv.getId())).getFirst();
        return List.of(
                EmailDto.builder()
                        .email(user.getEmail())
                        .subject(event.getSubject())
                        .body(String.format(
                                templateCache.getTemplate(getType()),
                                event.getSubject(),
                                user.getFirstName(),
                                event.firstName() + " " + event.lastName(),
                                adv.getTitle(),
                                event.commentUri(),
                                caption,
                                supportLink))
                        .build()
        );
    }
}
