package com.github.spjavaind300.profileservice.dto;

import com.github.spjavaind300.profileservice.annotation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePasswordDTO {

    @NotBlank(message = "Текущий пароль не может быть пустым")
    private String currentPassword;

    @ValidPassword
    private String newPassword;
}
