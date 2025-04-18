package com.github.spjavaind300.profileservice.controller;

import com.github.spjavaind300.profileservice.exception.AccessDeniedProfileException;
import com.github.spjavaind300.profileservice.exception.InvalidImageException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity<Void> handleInvalidImage() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(AccessDeniedProfileException.class)
    public ResponseEntity<Void> handleAccessDenied() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception ex, HttpServletRequest req) {
        // тут вы гарантированно получите запись в логе со стеком
        log.error("Unhandled exception for {} {}: {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);

        // возвращаете клиенту любое тело (можно ваше DTO)
        return ResponseEntity
                .status(500)
                .body(Map.of(
                        "status", 500,
                        "error", "Internal Server Error",
                        "message", ex.getMessage()
                ));
    }

}
