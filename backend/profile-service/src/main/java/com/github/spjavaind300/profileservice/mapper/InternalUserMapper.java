package com.github.spjavaind300.profileservice.mapper;


import com.github.spjavaind300.profileservice.dto.InternalProfileResponse;
import com.github.spjavaind300.profileservice.dto.InternalUserResponse;
import com.github.spjavaind300.profileservice.dto.InternalUserSummary;
import com.github.spjavaind300.profileservice.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class InternalUserMapper {
    public InternalUserResponse toResponse(UserDTO userDTO) {
        return new InternalUserResponse(
                userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.getEmail(),
                userDTO.getPhone()
        );
    }

    public InternalUserSummary toSummary(UserDTO userDTO) {
        return new InternalUserSummary(
                userDTO.getId(),
                userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.getEmail()
        );
    }

    public InternalProfileResponse toProfile(UserDTO userDTO) {
        return new InternalProfileResponse(
                userDTO.getId(),
                userDTO.getFirstName(),
                userDTO.getImage()
        );
    }
}
