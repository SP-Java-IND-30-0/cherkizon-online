package com.cherkizon.auth.dto.request;

public record RequestChangePasswordDto(
        String currentPassword,
        String newPassword
) {
}
