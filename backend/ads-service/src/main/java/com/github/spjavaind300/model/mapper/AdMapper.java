package com.github.spjavaind300.model.mapper;

import com.github.spjavaind300.model.dto.AdExtraInfoDto;
import com.github.spjavaind300.model.dto.AdRequestDto;
import com.github.spjavaind300.model.dto.AdResponseDto;
import com.github.spjavaind300.model.dto.ImageDto;
import com.github.spjavaind300.model.dto.UserDto;
import com.github.spjavaind300.model.entity.Ad;
import com.github.spjavaind300.model.event.AdUpdatedEvent;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AdMapper {

    @Mapping(target = "image", ignore = true)
    AdResponseDto toAdResponseDto(Ad ad);

    @Mapping(target = "image", ignore = true)
    AdExtraInfoDto toAdExtraInfoDto(Ad ad, UserDto userDto);

    @Mapping(target = "imageKey", source = "imageDto.url")
    @Mapping(target = "originalFilename", source = "imageDto.name")
    Ad fromAdRequestDto(AdRequestDto dto, ImageDto imageDto);

    void updateAd(@MappingTarget Ad ad, AdRequestDto dto);

    @Mapping(target = "advUri", source = "uri")
    AdUpdatedEvent toAdUpdatedEvent(Ad ad, UserDto userDto, String uri);


    @AfterMapping
    default void setImage(@MappingTarget AdResponseDto adResponseDto, Ad ad) {
        adResponseDto.setImage("/ads/"+ad.getImageKey());
    }

    @AfterMapping
    default void setImage(@MappingTarget AdExtraInfoDto adExtraInfoDto, Ad ad) {
        adExtraInfoDto.setImage("/ads/"+ad.getImageKey());
    }
}
