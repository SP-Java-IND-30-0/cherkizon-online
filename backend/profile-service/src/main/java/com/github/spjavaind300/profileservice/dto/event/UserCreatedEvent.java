package com.github.spjavaind300.profileservice.dto.event;

public record UserCreatedEvent(
        long id,
        String email,
        String firstName,
        String lastName,
        String phone
) {}
