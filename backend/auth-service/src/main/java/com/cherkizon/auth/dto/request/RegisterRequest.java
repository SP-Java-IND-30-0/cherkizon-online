package com.cherkizon.auth.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotNull
    @Email
    private String username;

    @NotNull //От 8 до 16 символов, одну заглавную, одну строчную буквыб одну цифру, латинские буквы
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,16}$")
    private String password;

    @NotNull
    @Size(min = 2, max = 16)
    private String firstName;

    @NotNull
    @Size(min = 2, max = 16)
    private String lastName;

    @Pattern(regexp = "\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}")
    private String phone;

    @NotNull
    private String role;
}
