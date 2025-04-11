package com.github.spjavaind300.model.mapper;

import com.github.spjavaind300.model.dto.AdExtraInfoDto;
import com.github.spjavaind300.model.dto.AdRequestDto;
import com.github.spjavaind300.model.dto.AdResponseDto;
import com.github.spjavaind300.model.dto.ImageDto;
import com.github.spjavaind300.model.dto.UserDto;
import com.github.spjavaind300.model.entity.Ad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AdMapper {

    @Mapping(target = "image", source = "imageUrl")
    AdResponseDto toAdResponseDto(Ad ad,String imageUrl);

    @Mapping(target = "image", source = "ad.imageKey")
    AdExtraInfoDto toAdExtraInfoDto(Ad ad, UserDto userDto);

    @Mapping(target = "imageKey", source = "imageDto.url")
    @Mapping(target = "originalFilename", source = "imageDto.name")
    Ad fromAdRequestDto(AdRequestDto dto, ImageDto imageDto);

    Ad updateAd(AdRequestDto dto);

}
