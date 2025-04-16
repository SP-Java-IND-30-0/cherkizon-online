package com.github.spjavaind300.profileservice.controller;

import com.github.spjavaind300.profileservice.exception.AccessDeniedProfileException;
import com.github.spjavaind300.profileservice.exception.AvatarUploadException;
import com.github.spjavaind300.profileservice.exception.InvalidImageException;
import com.github.spjavaind300.profileservice.exception.UserAuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAuthException.class)
    public ResponseEntity<Void> handleUserNotFound() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(AvatarUploadException.class)
    public ResponseEntity<Void> handleAvatarUploadException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity<Void> handleInvalidImage() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(AccessDeniedProfileException.class)
    public ResponseEntity<Void> handleAccessDenied() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleServerError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
