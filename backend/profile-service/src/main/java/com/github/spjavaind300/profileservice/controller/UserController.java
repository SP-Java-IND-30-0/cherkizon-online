package com.github.spjavaind300.profileservice.controller;



import com.github.spjavaind300.profileservice.dto.UpdatePasswordDTO;
import com.github.spjavaind300.profileservice.dto.UpdateUserDTO;
import com.github.spjavaind300.profileservice.dto.UserDTO;
import com.github.spjavaind300.profileservice.exception.InvalidImageException;
import com.github.spjavaind300.profileservice.security.CustomUserDetails;
import com.github.spjavaind300.profileservice.service.AvatarService;
import com.github.spjavaind300.profileservice.service.PasswordService;
import com.github.spjavaind300.profileservice.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {


    private final ProfileService profileService;
    private final PasswordService passwordService;
    private final AvatarService avatarService;


    @GetMapping("/me")
    public ResponseEntity<UserDTO> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserDTO userDTO = profileService.getProfile(userDetails.userId());
        return ResponseEntity.ok(userDTO);
    }


    @PatchMapping("/me")
    public ResponseEntity<UpdateUserDTO> updateProfile(@RequestBody @Valid UpdateUserDTO updatedData) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UpdateUserDTO result = profileService.updateProfile(userDetails.userId(), updatedData);
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


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();


        profileService.updateAvatar(userDetails.userId(), image);


        return ResponseEntity.ok().build();
    }


    @PatchMapping("/set_password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid UpdatePasswordDTO passwordDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        passwordService.changePassword(userDetails.userId(), passwordDTO);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();


        profileService.deleteProfile(userDetails.userId());


        return ResponseEntity.noContent().build();
    }

    @GetMapping(
            value    = "/avatar/{userId}/{filename:.+}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public ResponseEntity<byte[]> getAvatar(
            @PathVariable long userId,
            @PathVariable String filename
    ) {
        String avatarKey = userId + "/" + filename;
        byte[] imageData = avatarService.getFile(avatarKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(imageData.length);

        return ResponseEntity.ok().headers(headers).body(imageData);
    }
}
