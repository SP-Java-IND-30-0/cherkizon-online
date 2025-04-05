package com.github.spjavaind300.commentsservice.client;

import com.github.spjavaind300.commentsservice.config.FeignConfig;
import com.github.spjavaind300.commentsservice.dto.ProfileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "profile-service", configuration = FeignConfig.class)
public interface ProfileFeignClient {

    @GetMapping("api/profile/me")
    ProfileDto getCurrentProfile();

    @GetMapping("api/profile/{profileId}")
    ProfileDto getProfileById(@PathVariable("profileId") Integer profileId);
}