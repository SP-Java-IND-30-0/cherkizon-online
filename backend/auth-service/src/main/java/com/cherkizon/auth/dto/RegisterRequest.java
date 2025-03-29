package com.cherkizon.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на регистрацию")
public class RegisterRequest {

    @NotBlank(message = "Email cannot be blank")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
            message = "Invalid email format")
    @Schema(description = "Email", example = "user@example.com")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "Password must be at least 8 characters with at least one letter and one number")
    @Schema(description = "Пароль", example = "Password123")
    private String password;

    @NotBlank(message = "First name cannot be blank")
    @Schema(description = "Имя", example = "John")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Schema(description = "Фамилия", example = "Doe")
    private String lastName;

    @NotBlank(message = "Phone cannot be blank")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$",
            message = "Phone must be 10-15 digits with optional + prefix")
    @Schema(description = "Телефон", example = "+1234567890")
    private String phone;

    @Schema(description = "Роль пользователя", example = "USER", defaultValue = "USER")
    private String role = "USER";
}