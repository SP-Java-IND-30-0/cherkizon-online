package com.github.spjavaind300.notificationservice.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class UserDto {

    @EqualsAndHashCode.Include
    private long id;

    private String firstName;

    private String lastName;

    private String email;
}
