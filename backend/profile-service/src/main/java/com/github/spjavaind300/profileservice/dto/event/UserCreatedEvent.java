package com.github.spjavaind300.profileservice.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserCreatedEvent(
        @JsonProperty("id") long id,
        @JsonProperty("username") String email,
        @JsonProperty("firstname") String firstName,
        @JsonProperty("lastname") String lastName,
        @JsonProperty("phone") String phone
) {}
