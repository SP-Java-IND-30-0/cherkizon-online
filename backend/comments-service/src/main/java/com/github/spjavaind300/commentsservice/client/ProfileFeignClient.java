package com.github.spjavaind300.commentsservice.client;

import com.github.spjavaind300.commentsservice.config.FeignConfig;
import com.github.spjavaind300.commentsservice.model.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "profile-service", configuration = FeignConfig.class)
public interface ProfileFeignClient {
    @GetMapping("api/profile/me")
    UserDto getCurrentUser();
}
