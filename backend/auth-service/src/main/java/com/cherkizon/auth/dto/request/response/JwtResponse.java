package com.cherkizon.auth.dto.request.response;

import lombok.Data;

@Data
public record JwtResponse(String accessToken, String refreshToken) {
}
