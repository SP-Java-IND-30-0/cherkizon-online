package com.github.spjavaind300.notificationservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdvDto {

    private int id;
    private long userId;
    private String title;
}
