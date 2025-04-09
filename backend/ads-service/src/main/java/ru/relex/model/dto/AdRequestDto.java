package ru.relex.model.dto;


import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record AdRequestDto(

        @Size(min = 4, max = 32)
        String title,

        @PositiveOrZero
        int price,

        @Size(min = 8, max = 64)
        String description) {
}
