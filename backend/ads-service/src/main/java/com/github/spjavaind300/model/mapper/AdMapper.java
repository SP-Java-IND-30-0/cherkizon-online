package com.github.spjavaind300.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.github.spjavaind300.model.dto.AdExtraInfoDto;
import com.github.spjavaind300.model.dto.AdRequestDto;
import com.github.spjavaind300.model.dto.AdResponseDto;
import com.github.spjavaind300.model.dto.UserDto;
import com.github.spjavaind300.model.entity.Ad;

@Mapper(componentModel = "spring")
public interface AdMapper {

    @Mapping(target = "image", source = "imageUrl")
    AdResponseDto toAdResponseDto(Ad ad);

    @Mapping(target = "image", source = "ad.imageUrl")
    AdExtraInfoDto toAdExtraInfoDto(Ad ad, UserDto userDto);

    Ad fromAdRequestDto(AdRequestDto dto);

}
