package com.github.spjavaind300.profileservice.dto;

import lombok.Data;

@Data
public class UserDTO {
    private long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String role;
    private String image;
}
