package com.github.spjavaind300.notificationservice.service.imp;

import com.github.spjavaind300.notificationservice.client.ProfileServiceClient;
import com.github.spjavaind300.notificationservice.model.dto.UserDto;
import com.github.spjavaind300.notificationservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileServiceImp implements ProfileService {

    private final ProfileServiceClient client;

    @Override
    public List<UserDto> getProfiles(List<Long> userIds) {
        return new ArrayList<>(client.getProfiles(userIds));
    }
}
