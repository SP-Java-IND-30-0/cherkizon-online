package com.github.spjavaind300.profileservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class InternalUserSummary {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
}
