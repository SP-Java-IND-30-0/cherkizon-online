package com.github.spjavaind300.profileservice.mapper;

import com.github.spjavaind300.profileservice.dto.UpdateUserDTO;
import com.github.spjavaind300.profileservice.dto.UserDTO;
import com.github.spjavaind300.profileservice.model.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
public class UserMapper {

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
        if (user.getImage() != null) {
            String url = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("api/users/avatar/")
                    .path(user.getImage())
                    .toUriString();
            userDTO.setImage(url);
        }
        //TODO передавать значение из токена
        userDTO.setRole("ADMIN");
        return userDTO;
    }

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
