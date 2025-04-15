package com.github.spjavaind300.model.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdResponseDto {

    @JsonProperty("pk")
    private int id;

    private String title;

    private int price;

    @JsonProperty("author")
    private long userId;

    private String image;

}
