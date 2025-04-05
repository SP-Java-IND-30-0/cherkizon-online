package com.github.spjavaind300.notificationservice.service.imp;

import com.github.spjavaind300.notificationservice.model.dto.UserDto;
import com.github.spjavaind300.notificationservice.service.ProfileService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileServiceImp implements ProfileService {

    @Override
    public List<UserDto> getProfiles(List<Integer> userIds) {
        return List.of();
    }
}
