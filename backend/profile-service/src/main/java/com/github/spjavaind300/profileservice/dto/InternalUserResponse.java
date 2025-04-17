package com.github.spjavaind300.profileservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InternalUserResponse {
    private String firstName;

    private String lastName;

    private String email;

    private String phone;
}
