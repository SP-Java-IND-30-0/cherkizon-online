package com.github.spjavaind300.profileservice.controller;

import com.github.spjavaind300.profileservice.dto.InternalProfileResponse;
import com.github.spjavaind300.profileservice.dto.InternalUserResponse;
import com.github.spjavaind300.profileservice.dto.InternalUserSummary;
import com.github.spjavaind300.profileservice.dto.UserDTO;
import com.github.spjavaind300.profileservice.mapper.InternalUserMapper;
import com.github.spjavaind300.profileservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalUserController {
    private final ProfileService profileService;
    private final InternalUserMapper mapper;

    @GetMapping("/user/{userId}")
    public ResponseEntity<InternalUserResponse> getUser(@PathVariable Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        UserDTO userDTO = profileService.getProfile(userId);
        return ResponseEntity.ok(mapper.toResponse(userDTO));
    }

    @GetMapping("/users")
    public ResponseEntity<List<InternalUserSummary>> getUsers(@RequestParam List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<InternalUserSummary> list = ids.stream()
                .map(id -> mapper.toSummary(profileService.getProfile(id)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<InternalProfileResponse> getProfile(@PathVariable Long profileId) {
        if (profileId == null || profileId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        UserDTO u = profileService.getProfile(profileId);
        return ResponseEntity.ok(mapper.toProfile(u));
    }
}
