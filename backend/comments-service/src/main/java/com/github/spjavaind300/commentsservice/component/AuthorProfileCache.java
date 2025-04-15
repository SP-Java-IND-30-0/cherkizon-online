package com.github.spjavaind300.commentsservice.component;

import com.github.spjavaind300.commentsservice.dto.ProfileDto;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthorProfileCache {

    private final Map<Long, ProfileDto> authorProfileMap = new ConcurrentHashMap<>();

    public void put(long authorId, ProfileDto profile) {
        authorProfileMap.put(authorId, profile);
    }

    public ProfileDto get(long authorId) {
        return authorProfileMap.get(authorId);
    }

    public void remove(long authorId) {
        authorProfileMap.remove(authorId);
    }
}