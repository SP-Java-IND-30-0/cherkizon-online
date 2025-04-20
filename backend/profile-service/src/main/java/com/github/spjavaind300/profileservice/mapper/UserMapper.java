package com.github.spjavaind300.profileservice.mapper;

import com.github.spjavaind300.profileservice.dto.UpdateUserDTO;
import com.github.spjavaind300.profileservice.dto.UserDTO;
import com.github.spjavaind300.profileservice.model.entity.User;
import com.github.spjavaind300.profileservice.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Mapper for converting between User entities and DTOs.
 */
@Component
public class UserMapper {

    /**
     * Converts a User entity to an UpdateUserDTO.
     *
     * @param user the User entity
     * @return an UpdateUserDTO or null if user is null
     */
    public UpdateUserDTO toUpdateUserDTO(User user) {
        if (user == null) {
            return null;
        }
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setFirstName(user.getFirstName());
        updateUserDTO.setLastName(user.getLastName());
        updateUserDTO.setPhone(user.getPhone());
        return updateUserDTO;
    }

    /**
     * Converts a User entity to a UserDTO, including image URL and role.
     *
     * @param user the User entity
     * @return a UserDTO or null if user is null
     */
    public UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setPhone(user.getPhone());
        String key = user.getImage();
        if (key != null && !key.isEmpty()) {
            userDTO.setImage("/users/avatar/" + user.getId());
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        userDTO.setRole(String.valueOf(userDetails.role()));
        return userDTO;
    }

    /**
     * Updates a User entity with values from an UpdateUserDTO.
     *
     * @param dto  the UpdateUserDTO with new data
     * @param user the User entity to update
     */
    public void toUpdatedUserEntity(UpdateUserDTO dto, User user) {
        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
    }
}

