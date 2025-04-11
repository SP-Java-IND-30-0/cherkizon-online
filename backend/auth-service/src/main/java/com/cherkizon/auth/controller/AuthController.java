package com.cherkizon.auth.controller;

import com.cherkizon.auth.dto.request.LoginRequest;
import com.cherkizon.auth.dto.request.RegisterRequest;
import com.cherkizon.auth.dto.request.response.JwtResponse;
import com.cherkizon.auth.dto.request.response.UserResponse;
import com.cherkizon.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "Регистрация и аутентификация пользователей")
@CrossOrigin("http://localhost:3000")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Регистрация пользователя",
            description = "Регистрирует нового пользователя и возвращает его данные")
    @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован")
    @ApiResponse(responseCode = "400", description = "Некорректные входные данные")
    @ApiResponse(responseCode = "409", description = "Пользователь с таким username уже существует")
    @PostMapping("/register")
    public UserResponse register(
            @Valid @RequestBody RegisterRequest request) {
        // TODO: Реализовать регистрацию
        
    }

    @Operation(summary = "Аутентификация пользователя",
            description = "Выполняет вход и возвращает JWT токены")
    @ApiResponse(responseCode = "200", description = "Успешная аутентификация")
    @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
    @PostMapping("/login")
    public JwtResponse login(
            @Valid @RequestBody LoginRequest request) {
        // TODO: Реализовать аутентификацию
    
    }

    @Operation(summary = "Обновление токена",
            description = "Возвращает новый access-токен по refresh-токену")
    @ApiResponse(responseCode = "200", description = "Токен обновлен")
    @ApiResponse(responseCode = "401", description = "Невалидный refresh-токен")
    @PostMapping("/refresh")
    public JwtResponse refreshToken(
            @RequestHeader("Authorization") String refreshToken) {
        // TODO: Реализовать обновление токена
        
    }
}
