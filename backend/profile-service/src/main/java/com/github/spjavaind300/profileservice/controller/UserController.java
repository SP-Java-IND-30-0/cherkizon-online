package com.github.spjavaind300.profileservice.controller;

import com.github.spjavaind300.profileservice.dto.UpdateUserDTO;
import com.github.spjavaind300.profileservice.dto.UserDTO;
import com.github.spjavaind300.profileservice.exception.UserNotFoundAuthException;
import com.github.spjavaind300.profileservice.service.impl.ProfileServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/users")
public class UserController {

    private final ProfileServiceImpl profileService;

    public UserController(ProfileServiceImpl profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getProfile(@RequestParam("userId") Long userId) {
        UserDTO userDTO = profileService.getProfile(userId);
        return ResponseEntity.ok(userDTO);
    }

    @PatchMapping("/me")
    public ResponseEntity<UpdateUserDTO> updateProfile(
            @RequestParam("userId") Long userId,
            @RequestBody @Valid UpdateUserDTO updatedData
    ) {
        UpdateUserDTO result = profileService.updateProfile(userId, updatedData);
        return ResponseEntity.ok(result);
    }

    @PatchMapping(value = "/me/image", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateAvatar(@RequestParam("userId") Long userId,
                                               @RequestParam("image") MultipartFile image) {
        try {
            String avatarUrl = profileService.saveOrUpdateAvatar(userId, image);
            return ResponseEntity.ok("Avatar updated successfully: " + avatarUrl);
        } catch (UserNotFoundAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());

        }
    }
}
