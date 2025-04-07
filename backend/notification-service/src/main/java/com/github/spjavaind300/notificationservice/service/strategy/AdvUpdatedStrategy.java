package com.github.spjavaind300.notificationservice.service.strategy;

import com.github.spjavaind300.notificationservice.model.NotificationType;
import com.github.spjavaind300.notificationservice.model.dto.AdvUpdatedEvent;
import com.github.spjavaind300.notificationservice.model.dto.EmailDto;
import com.github.spjavaind300.notificationservice.model.dto.UserDto;
import com.github.spjavaind300.notificationservice.service.CommentService;
import com.github.spjavaind300.notificationservice.service.NotificationStrategy;
import com.github.spjavaind300.notificationservice.service.ProfileService;
import com.github.spjavaind300.notificationservice.utils.EmailTemplateCache;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdvUpdatedStrategy implements NotificationStrategy<AdvUpdatedEvent> {

    private final String caption;

    private final String supportLink;

    private final EmailTemplateCache templateCache;

    private final CommentService commentService;

    private final ProfileService profileService;


    public AdvUpdatedStrategy(
            @Qualifier("caption") String caption,
            @Qualifier("supportLink") String supportLink,
            EmailTemplateCache templateCache,
            CommentService commentService, ProfileService profileService) {
        this.caption = caption;
        this.supportLink = supportLink;
        this.templateCache = templateCache;
        this.commentService = commentService;
        this.profileService = profileService;
    }


    @Override
    public NotificationType getType() {
        return NotificationType.ADV_UPDATED;
    }

    @Override
    public List<EmailDto> prepareEmail(AdvUpdatedEvent event) {
        List<Integer> commentAuthorIds = commentService.getCommentsAuthorIdsOfAdv(event.id());
        List<UserDto> users = profileService.getProfiles(commentAuthorIds);
        List<EmailDto> emails = new ArrayList<>();
        for (UserDto user : users) {
            emails.add(EmailDto.builder()
                    .email(user.getEmail())
                    .subject(event.getSubject())
                    .body(String.format(
                            templateCache.getTemplate(getType()),
                            event.getSubject(),
                            user.getFirstName(),
                            event.title(),
                            event.title(),
                            event.advUri(),
                            caption,
                            supportLink
                            ))
                    .build());
        }
        return emails;
    }
}
