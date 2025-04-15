package com.github.spjavaind300.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Schema(description = "Data for ad creation")
public record AdRequestDto(

        @Size(min = 4, max = 32)
        String title,

        @PositiveOrZero
        Integer price,

        @Size(min = 8, max = 64)
        String description) {
}
