package com.github.spjavaind300.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDto(
        @JsonProperty("firstName") String authorFirstName,
        @JsonProperty("lastName") String authorLastName,
        String email,
        String phone
) {
}
