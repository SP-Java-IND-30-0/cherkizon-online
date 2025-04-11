package com.github.spjavaind300.service;

import com.github.spjavaind300.model.dto.UserDto;

public interface ProfileService {

    UserDto getUser(long userId);
}
