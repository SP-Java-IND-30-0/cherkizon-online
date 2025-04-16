package com.github.spjavaind300.commentsservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserContext {

    @NotNull
    private long authorId;

    @NotNull
    private Role role;
}