package com.github.spjavaind300.controller;

import com.github.spjavaind300.model.dto.AdExtraInfoDto;
import com.github.spjavaind300.model.dto.AdRequestDto;
import com.github.spjavaind300.model.dto.AdResponseDto;
import com.github.spjavaind300.model.dto.ListAdsDto;
import com.github.spjavaind300.model.dto.UserContext;
import com.github.spjavaind300.service.AdService;
import com.github.spjavaind300.service.ImageStorageService;
import com.github.spjavaind300.service.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ads")
@RequiredArgsConstructor
public class AdController {

    private final AdService adService;
    private final ImageStorageService imageStorageService;
    private final JwtUtils jwtUtils;


    @GetMapping
    public ListAdsDto getAllAds() {
        return adService.getAllAds();
    }

    @GetMapping("/me")
    public ListAdsDto getAllAdsForUser(HttpServletRequest request) {
        UserContext userContext = jwtUtils.getUserContext(request);
        return adService.getAllAdsForUser(userContext.userId());
    }

    @GetMapping("/{id}")
    public AdExtraInfoDto getAdInfo(@PathVariable int id) {
        return adService.getAdInfo(id);
    }

    @PostMapping
    public ResponseEntity<AdResponseDto> createAd(HttpServletRequest request, @RequestPart @Valid AdRequestDto adRequestDto, @RequestPart @NotNull MultipartFile image) {
        UserContext userContext = jwtUtils.getUserContext(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(adService.createAd(userContext.userId(), adRequestDto, image));
    }

    @PatchMapping("/{id}")
    public AdResponseDto updateAd(HttpServletRequest request, @PathVariable int id, @RequestBody @Valid AdRequestDto adRequestDto) {
        UserContext userContext = jwtUtils.getUserContext(request);
        return adService.updateAd(id, adRequestDto, userContext);
    }

    @PatchMapping("/{id}/image")
    public byte[] updateAdImage(HttpServletRequest request, @PathVariable int id, @RequestPart @NotNull MultipartFile image) {
        UserContext userContext = jwtUtils.getUserContext(request);
        return adService.updateImage(id, image, userContext);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(HttpServletRequest request, @PathVariable int id) {
        UserContext userContext = jwtUtils.getUserContext(request);
        adService.deleteAd(id, userContext);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/image/{imageKey}")
    public byte[] getAdImage(@PathVariable String imageKey) {
        return imageStorageService.getFile("image/" + imageKey);
    }


}
