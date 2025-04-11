package com.github.spjavaind300.service.imp;

import com.github.spjavaind300.model.dto.ListAdsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdServiceImp {


    public ListAdsDto getAllAds() {
        return ListAdsDto.builder().build();
    }
}
