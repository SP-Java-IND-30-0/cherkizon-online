package com.github.spjavaind300.notificationservice.service;

import com.github.spjavaind300.notificationservice.model.dto.UserDto;

import java.util.List;

public interface ProfileService {

    List<UserDto> getProfiles(List<Integer> userIds);
}
