package com.cherkizon.auth.controller;

import com.cherkizon.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/auth")
@RequiredArgsConstructor
public class InternalController {

    private AuthService authService;


    @PatchMapping("/{userId}/set_password")
    public ResponseEntity<Void> setPassword(@PathVariable("userId") Long userId, @RequestBody RequestChangePasswordDto passwords) {

        authService.changePassword(userId, passwords);

        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {

        authService.deleteUser(userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
