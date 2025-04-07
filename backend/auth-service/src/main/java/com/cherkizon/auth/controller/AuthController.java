package com.cherkizon.auth.controller;

import com.cherkizon.auth.dto.request.LoginRequest;
import com.cherkizon.auth.dto.request.RegisterRequest;
import com.cherkizon.auth.dto.request.response.JwtResponse;
import com.cherkizon.auth.dto.request.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth API", description = "Регистрация и аутентификация пользователей")
public class AuthController {

    @Operation(summary = "Регистрация", description = "Создает нового пользователя")
    @PostMapping("/register")
    public UserResponse register(@RequestBody RegisterRequest request) {
        return new UserResponse();
    }

    @Operation(summary = "Логин", description = "Аутентификация и получение JWT-токена")
    @PostMapping("/login")
    public JwtResponse login(@RequestBody LoginRequest request) {
        return new JwtResponse();
    }
}