package com.github.spjavaind300.service.imp;

import com.github.spjavaind300.exception.NotFoundException;
import com.github.spjavaind300.model.dto.AdExtraInfoDto;
import com.github.spjavaind300.model.dto.AdRequestDto;
import com.github.spjavaind300.model.dto.AdResponseDto;
import com.github.spjavaind300.model.dto.ImageDto;
import com.github.spjavaind300.model.dto.ListAdsDto;
import com.github.spjavaind300.model.dto.UserDto;
import com.github.spjavaind300.model.entity.Ad;
import com.github.spjavaind300.model.mapper.AdMapper;
import com.github.spjavaind300.repository.AdRepository;
import com.github.spjavaind300.service.AdService;
import com.github.spjavaind300.service.ImageStorageService;
import com.github.spjavaind300.service.ProfileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdServiceImp implements AdService {

    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final ImageStorageService imageStorageService;
    private final ProfileService profileService;


    @Override
    public ListAdsDto getAllAds() {
        List<Ad> ads = adRepository.findAll();
        return ListAdsDto.builder()
                .count(ads.size())
                .items(ads.stream()
                        .map(ad -> adMapper.toAdResponseDto(
                                ad,
                                imageStorageService.getPreSignedUrl(ad.getImageKey(), Duration.ofMinutes(15)))
                        )
                        .toList())
                .build();
    }

    @Override
    public ListAdsDto getAllAdsForUser(long userId) {
        List<Ad> ads = adRepository.findAllByUserId(userId);
        return ListAdsDto.builder()
                .count(ads.size())
                .items(ads.stream()
                        .map(ad -> adMapper.toAdResponseDto(
                                ad,
                                imageStorageService.getPreSignedUrl(ad.getImageKey(), Duration.ofMinutes(15)))
                        )
                        .toList())
                .build();
    }

    @Override
    public AdExtraInfoDto getAdInfo(int id) {
        Ad ad = adRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        UserDto userDto = profileService.getUser(ad.getUserId());
        return adMapper.toAdExtraInfoDto(ad, userDto);
    }

    @Transactional
    @Override
    public AdResponseDto createAd(long userId, AdRequestDto adRequestDto, MultipartFile image) {
        ImageDto imageDto = imageStorageService.uploadFile(image);
        Ad ad = adMapper.fromAdRequestDto(adRequestDto, imageDto);
        ad.setUserId(userId);
        return saveAd(ad);
    }

    @Transactional
    @Override
    public void deleteAd(int id) {
        Ad ad = adRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        imageStorageService.deleteFile(ad.getImageKey());
        adRepository.delete(ad);
    }

    @Transactional
    @Override
    public void deleteAllByUserId(long id) {
        List<Ad> ads = adRepository.findAllByUserId(id);
        ads.forEach(ad -> deleteAd(ad.getId()));
    }

    @Transactional
    @Override
    public AdResponseDto updateAd(int id, AdRequestDto adRequestDto) {
        Ad ad = adRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        Ad updateAd = adMapper.updateAd(ad, adRequestDto);
        return saveAd(updateAd);
    }

    @Transactional
    @Override
    public byte[] updateImage(int id, MultipartFile image) {
        Ad ad = adRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        ad.setImageKey(imageStorageService.uploadFile(image).url());
        ad.setOriginalFilename(image.getOriginalFilename());
        adRepository.save(ad);
        return imageStorageService.getFile(ad.getImageKey());
    }

    private AdResponseDto saveAd(Ad ad) {

        return adMapper.toAdResponseDto(
                adRepository.save(ad),
                imageStorageService.getPreSignedUrl(ad.getImageKey(), Duration.ofMinutes(15))
        );
    }
}
