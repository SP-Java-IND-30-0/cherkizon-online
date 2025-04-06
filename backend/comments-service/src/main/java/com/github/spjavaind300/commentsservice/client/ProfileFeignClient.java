package com.github.spjavaind300.commentsservice.client;

import com.github.spjavaind300.commentsservice.dto.ProfileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "profile-service", url = "${custom.profile-service-url}")
public interface ProfileFeignClient {

    @GetMapping("/api/profile/me")
    ProfileDto getCurrentProfile();
}