package com.github.spjavaind300.profileservice.client;

import com.github.spjavaind300.profileservice.dto.UpdatePasswordDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @PostMapping("/internal/auth/{userId}/change-password")
    void changePassword(@PathVariable("userId") Long userId,
                        @RequestBody UpdatePasswordDTO request);
}
