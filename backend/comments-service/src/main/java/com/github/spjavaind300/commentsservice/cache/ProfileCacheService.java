package com.github.spjavaind300.commentsservice.cache;

import com.github.spjavaind300.commentsservice.dto.ProfileDto;

public interface ProfileCacheService {

    ProfileDto getProfile(long authorId);
}