package com.github.spjavaind300.profileservice.mapper;


import com.github.spjavaind300.profileservice.dto.InternalProfileResponse;
import com.github.spjavaind300.profileservice.dto.InternalUserResponse;
import com.github.spjavaind300.profileservice.dto.InternalUserSummary;
import com.github.spjavaind300.profileservice.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class InternalUserMapper {
    public InternalUserResponse toResponse(User user) {
        return new InternalUserResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone()
        );
    }

    public InternalUserSummary toSummary(User user) {
        return new InternalUserSummary(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }

    public InternalProfileResponse toProfile(User user) {
        String url = null;
        String key = user.getImage();
        if (key != null && !key.isEmpty()) {
            url = "/users/avatar/" + user.getId();
        }
        return new InternalProfileResponse(
                user.getId(),
                user.getFirstName(),
                url
        );
    }
}
