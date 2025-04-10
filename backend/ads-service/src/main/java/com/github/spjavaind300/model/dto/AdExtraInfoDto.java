package com.github.spjavaind300.model.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AdExtraInfoDto {

    @JsonProperty("pk")
    private int id;

    private String title;

    private int price;

    private String description;

    private String image;

    private String authorFirstName;

    private String authorLastName;

    private String email;

    private String phone;

}
