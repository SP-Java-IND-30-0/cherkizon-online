package com.github.spjavaind300.profileservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InternalProfileResponse {
    private long id;
    private String firstName;
    private String image;
}
