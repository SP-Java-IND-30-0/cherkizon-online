package com.github.spjavaind300.profileservice.controller;

import com.github.spjavaind300.profileservice.exception.AccessDeniedProfileException;
import com.github.spjavaind300.profileservice.exception.AvatarNotFoundException;
import com.github.spjavaind300.profileservice.exception.InvalidImageException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity<String> handleInvalidImage(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(AccessDeniedProfileException.class)
    public ResponseEntity<String> handleAccessDenied(Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(AvatarNotFoundException.class)
    public ResponseEntity<Void> handleAvatarNotFound(AvatarNotFoundException e) {
        log.warn("Avatar not found: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();
    }

}
