package com.github.spjavaind300.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Builder
public class ListAdsDto {

    @Getter
    private int count;
    private List<AdResponseDto> items;

    public List<AdResponseDto> getItems() {
        return Collections.unmodifiableList(items);
    }
}
