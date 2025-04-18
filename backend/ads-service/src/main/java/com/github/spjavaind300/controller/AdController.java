package com.github.spjavaind300.controller;

import com.github.spjavaind300.model.dto.AdExtraInfoDto;
import com.github.spjavaind300.model.dto.AdRequestDto;
import com.github.spjavaind300.model.dto.AdResponseDto;
import com.github.spjavaind300.security.CustomUserDetails;
import com.github.spjavaind300.service.AdService;
import com.github.spjavaind300.service.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ads")
@RequiredArgsConstructor
@Slf4j
public class AdController {

    private final AdService adService;
    private final ImageStorageService imageStorageService;


    @GetMapping
    public Map<String, List<AdResponseDto>> getAllAds() {

        return Map.of(
                "results",
                adService.getAllAds().getItems()
        );
    }

    @GetMapping("/me")
    public Map<String, List<AdResponseDto>> getAllAdsForUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return Map.of(
                "results",
                adService.getAllAdsForUser(userDetails.userId()).getItems()
        );
    }

    @GetMapping("/{id}")
    public AdExtraInfoDto getAdInfo(@PathVariable int id) {
        return adService.getAdInfo(id);
    }

    @Operation(summary = "Create new advertisement",
            description = "Create new ad with image upload")
    @ApiResponse(responseCode = "201", description = "Ad created successfully",
            content = @Content(schema = @Schema(implementation = AdResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdResponseDto> createAd(
            @Parameter(description = "Ad properties",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdRequestDto.class)))
            @RequestPart(value = "properties")
            @Valid
            AdRequestDto adRequestDto,

            @Parameter(description = "Image file",
                    content = @Content(mediaType = "image/*",
                            schema = @Schema(type = "string", format = "binary")))
            @RequestPart("image")
            @NotNull
            MultipartFile image) {

        if (image.getContentType() == null || !image.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Invalid image file type");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adService.createAd(userDetails.userId(), adRequestDto, image));
    }

    @PatchMapping("/{id}")
    public AdResponseDto updateAd(@PathVariable int id,
                                  @RequestBody @Valid AdRequestDto adRequestDto) {
        return adService.updateAd(id, adRequestDto);
    }

    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> updateAdImage(@PathVariable int id,
                                                @RequestPart("image") @NotNull MultipartFile image) {

        if (image.getContentType() == null || !image.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Invalid image file type");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(image.getContentType()));
        byte[] imageData = adService.updateImage(id, image);
        headers.setContentLength(imageData.length);
        return ResponseEntity.ok().headers(headers).body(imageData);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable int id) {
        adService.deleteAd(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/images/{imageKey}")
    public ResponseEntity<byte[]> getAdImage(@PathVariable String imageKey) {
        byte[] imageData = imageStorageService.getFile("images/" + imageKey);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(imageData.length);
        return ResponseEntity.ok().headers(headers).body(imageData);

    }


}
