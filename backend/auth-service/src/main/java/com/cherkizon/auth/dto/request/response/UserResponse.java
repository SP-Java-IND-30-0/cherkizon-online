package com.cherkizon.auth.dto.request.response;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String phone;
}
