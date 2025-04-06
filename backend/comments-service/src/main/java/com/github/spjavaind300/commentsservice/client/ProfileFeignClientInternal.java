package com.github.spjavaind300.commentsservice.client;

import com.github.spjavaind300.commentsservice.dto.ProfileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@FeignClient(name = "profile-service", url = "${custom.profile-service-url}")
public interface ProfileFeignClientInternal {

    @GetMapping("/internal/profile/me")
    ProfileDto getCurrentProfileInternal();

    @GetMapping("/internal/profile/{profileId}")
    ProfileDto getProfileByIdInternal(@PathVariable("profileId") Long profileId);

    @GetMapping("/internal/profile/comments/{announcementId}")
    List<ProfileDto> getProfilesByAnnouncementId(@PathVariable("announcementId") Long announcementId);
}