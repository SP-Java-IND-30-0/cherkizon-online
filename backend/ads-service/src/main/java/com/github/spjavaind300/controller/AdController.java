package com.github.spjavaind300.controller;

import com.github.spjavaind300.model.dto.AdExtraInfoDto;
import com.github.spjavaind300.model.dto.AdRequestDto;
import com.github.spjavaind300.model.dto.AdResponseDto;
import com.github.spjavaind300.model.dto.ListAdsDto;
import com.github.spjavaind300.model.dto.UserContext;
import com.github.spjavaind300.service.AdService;
import com.github.spjavaind300.service.ImageStorageService;
import com.github.spjavaind300.service.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@Slf4j
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

    @Operation(summary = "Create new advertisement",
            description = "Create new ad with image upload")
    @ApiResponse(responseCode = "201", description = "Ad created successfully",
            content = @Content(schema = @Schema(implementation = AdResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdResponseDto> createAd(
            @Parameter(hidden = true)
            HttpServletRequest request,

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

        UserContext userContext = jwtUtils.getUserContext(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adService.createAd(userContext.userId(), adRequestDto, image));
    }

    @PatchMapping("/{id}")
    public AdResponseDto updateAd(HttpServletRequest request,
                                  @PathVariable int id,
                                  @RequestBody @Valid AdRequestDto adRequestDto) {
        UserContext userContext = jwtUtils.getUserContext(request);
        return adService.updateAd(id, adRequestDto, userContext);
    }

    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> updateAdImage(HttpServletRequest request,
                                @PathVariable int id,
                                @RequestPart("image") @NotNull MultipartFile image) {

        if (image.getContentType() == null || !image.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Invalid image file type");
        }

        UserContext userContext = jwtUtils.getUserContext(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(image.getContentType()));
        byte[] imageData= adService.updateImage(id, image, userContext);
        headers.setContentLength(imageData.length);
        return ResponseEntity.ok().headers(headers).body(imageData);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(HttpServletRequest request, @PathVariable int id) {
        UserContext userContext = jwtUtils.getUserContext(request);
        adService.deleteAd(id, userContext);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/images/{imageKey}")
    public ResponseEntity<byte[]> getAdImage(@PathVariable String imageKey) {
        byte[] imageData= imageStorageService.getFile("images/" + imageKey);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(imageData.length);
        return ResponseEntity.ok().headers(headers).body(imageData);

    }


}
