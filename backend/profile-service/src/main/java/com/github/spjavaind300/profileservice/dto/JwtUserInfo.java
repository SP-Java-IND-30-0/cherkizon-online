package com.github.spjavaind300.profileservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtUserInfo {
    private long userId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Role role;

}
