package com.github.spjavaind300.profileservice.controller;

import com.github.spjavaind300.profileservice.dto.JwtUserInfo;
import com.github.spjavaind300.profileservice.dto.UpdateUserDTO;
import com.github.spjavaind300.profileservice.dto.UserDTO;
import com.github.spjavaind300.profileservice.exception.InvalidImageException;
import com.github.spjavaind300.profileservice.service.impl.JwtServiceImpl;
import com.github.spjavaind300.profileservice.service.impl.ProfileServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final ProfileServiceImpl profileService;
    private final JwtServiceImpl jwtService;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getProfile() {
        JwtUserInfo jwtInfo = jwtService.parseToken("Токен");
        long userId = jwtInfo.getUserId();
        UserDTO userDTO = profileService.getProfile(userId);
        return ResponseEntity.ok(userDTO);
    }

    @PatchMapping("/me")
    public ResponseEntity<UpdateUserDTO> updateProfile(@RequestBody @Valid UpdateUserDTO updatedData) {
        JwtUserInfo jwtInfo = jwtService.parseToken("Токен");
        long userId = jwtInfo.getUserId();
        UpdateUserDTO result = profileService.updateProfile(userId, updatedData);
        return ResponseEntity.ok(result);
    }

    @PatchMapping(value = "/me/image", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateAvatar(@RequestPart("image") MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new InvalidImageException();
        }

        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidImageException();
        }

        JwtUserInfo jwtInfo = jwtService.parseToken("Токен");
        long userId = jwtInfo.getUserId();

        profileService.updateAvatar(userId, image);

        return ResponseEntity.ok().build();
    }

    //TODO добавить эндпоинт на смену пароля и на удаление пользователя

}
