package com.github.spjavaind300.service;

import com.github.spjavaind300.model.dto.AdExtraInfoDto;
import com.github.spjavaind300.model.dto.AdRequestDto;
import com.github.spjavaind300.model.dto.AdResponseDto;
import com.github.spjavaind300.model.dto.ListAdsDto;
import com.github.spjavaind300.model.dto.UserContext;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface AdService {
    ListAdsDto getAllAds();

    ListAdsDto getAllAdsForUser(long userId);

    AdExtraInfoDto getAdInfo(int id);

    @Transactional
    AdResponseDto createAd(long userId, AdRequestDto adRequestDto, MultipartFile image);

    @Transactional
    void deleteAd(int id, UserContext userContext);

    @Transactional
    AdResponseDto updateAd(int id, AdRequestDto adRequestDto, UserContext userContext);

    @Transactional
    byte[] updateImage(int id, MultipartFile image, UserContext userContext);

    @Transactional
    void deleteAllByUserId(long id, UserContext userContext);

}
