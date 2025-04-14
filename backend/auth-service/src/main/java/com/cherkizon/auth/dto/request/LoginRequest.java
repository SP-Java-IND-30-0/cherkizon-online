package com.cherkizon.auth.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LoginRequest {
    @NotNull
    @Email
    private String username;

    @NotNull  //От 8 до 16 символов, одну заглавную, одну строчную буквыб одну цифру, латинские буквы
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,16}$")
    private String password;
}
