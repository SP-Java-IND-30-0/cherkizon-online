package com.cherkizon.auth.dto.request.response;

import lombok.Data;

@Data
public class JwtResponse {
    private final String accessToken;
   private final String refreshToken;


}
