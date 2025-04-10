package com.github.spjavaind300.model.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AdResponseDto {

    @JsonProperty("pk")
    private int id;

    private String title;

    private int price;

    @JsonProperty("author")
    private int userId;

    private String image;

}
