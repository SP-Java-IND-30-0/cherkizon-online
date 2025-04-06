package com.github.spjavaind300.notificationservice.client;

import com.github.spjavaind300.notificationservice.model.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@FeignClient(name = "profile-service")
public interface ProfileServiceClient {


    @GetMapping("/internal/profile")
    Set<UserDto> getProfiles(@RequestParam(value = "ids") List<Integer> ids);
}
