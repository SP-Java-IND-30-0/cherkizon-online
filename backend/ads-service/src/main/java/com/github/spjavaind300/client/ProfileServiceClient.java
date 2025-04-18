package com.github.spjavaind300.client;

import com.github.spjavaind300.model.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "profile-service")
public interface ProfileServiceClient {

    @GetMapping("/internal/user/{userId}")
    UserDto getProfiles(@PathVariable long userId);
}