package com.github.spjavaind300.model.dto;

import lombok.Builder;

import java.util.List;

@Builder
public class ListAdsDto {

    private int count;
    private List<AdResponseDto> items;
}
