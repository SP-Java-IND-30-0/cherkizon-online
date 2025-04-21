package com.github.spjavaind300.profileservice.mapper;

import com.github.spjavaind300.profileservice.dto.InternalProfileResponse;
import com.github.spjavaind300.profileservice.dto.InternalUserResponse;
import com.github.spjavaind300.profileservice.dto.InternalUserSummary;
import com.github.spjavaind300.profileservice.model.entity.User;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting User entities to internal data transfer objects
 * used by the advertising service.
 */
@Component
public class InternalUserMapper {

    /**
     * Converts a User entity to an InternalUserResponse.
     *
     * @param user the User entity
     * @return an InternalUserResponse containing basic user details
     */
    public InternalUserResponse toResponse(User user) {
        return new InternalUserResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone()
        );
    }

    /**
     * Converts a User entity to an InternalUserSummary.
     *
     * @param user the User entity
     * @return an InternalUserSummary containing user ID, name, and email
     */
    public InternalUserSummary toSummary(User user) {
        return new InternalUserSummary(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }

    /**
     * Converts a User entity to an InternalProfileResponse.
     *
     * @param user the User entity
     * @return an InternalProfileResponse with user ID, name, and avatar URL if set
     */
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
