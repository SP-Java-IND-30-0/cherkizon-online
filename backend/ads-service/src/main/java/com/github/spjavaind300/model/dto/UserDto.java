package com.github.spjavaind300.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDto(
        @JsonProperty("first_name") String authorFirstName,
        @JsonProperty("last_name") String authorLastName,
        String email,
        String phone
) {
}
