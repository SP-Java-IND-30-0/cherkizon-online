package com.github.spjavaind300.profileservice.controller;

import com.github.spjavaind300.profileservice.dto.InternalProfileResponse;
import com.github.spjavaind300.profileservice.dto.InternalUserResponse;
import com.github.spjavaind300.profileservice.dto.InternalUserSummary;

import com.github.spjavaind300.profileservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalUserController {
    private final ProfileService profileService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<InternalUserResponse> getUser(@PathVariable long userId) {
        InternalUserResponse response = profileService.getUserForAdsRequest(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<List<InternalUserSummary>> getUsers(@RequestParam List<Long> ids) {
        List<InternalUserSummary> summaries = profileService.getUserSummariesForAdsRequest(ids);
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<InternalProfileResponse> getProfile(@PathVariable Long profileId) {
        InternalProfileResponse internalProfileResponse =
                profileService.getProfileForAdsRequest(profileId);
        return ResponseEntity.ok(internalProfileResponse);
    }
}
