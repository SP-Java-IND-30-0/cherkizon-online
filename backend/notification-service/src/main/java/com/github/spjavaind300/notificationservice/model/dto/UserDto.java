package com.github.spjavaind300.notificationservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @EqualsAndHashCode.Include
    private long id;

    private String firstName;

    private String lastName;

    private String email;
}
