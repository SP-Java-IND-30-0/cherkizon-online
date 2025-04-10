package ru.relex.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.relex.model.dto.AdExtraInfoDto;
import ru.relex.model.dto.AdRequestDto;
import ru.relex.model.dto.AdResponseDto;
import ru.relex.model.dto.UserDto;
import ru.relex.model.entity.Ad;

@Mapper(componentModel = "spring")
public interface AdMapper {

    @Mapping(target = "image", source = "imageUrl")
    AdResponseDto toAdResponseDto(Ad ad);

    @Mapping(target = "image", source = "ad.imageUrl")
    AdExtraInfoDto toAdExtraInfoDto(Ad ad, UserDto userDto);

    Ad fromAdRequestDto(AdRequestDto dto);

}
