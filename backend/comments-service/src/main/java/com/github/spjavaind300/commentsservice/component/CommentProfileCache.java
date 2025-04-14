package com.github.spjavaind300.commentsservice.component;

import com.github.spjavaind300.commentsservice.dto.ProfileDto;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CommentProfileCache {

    private final Map<Integer, ProfileDto> commentProfileMap = new ConcurrentHashMap<>();

    public void put(int commentId, ProfileDto profile) {
        commentProfileMap.put(commentId, profile);
    }

    public ProfileDto get(int commentId) {
        return commentProfileMap.get(commentId);
    }

    public void remove(int commentId) {
        commentProfileMap.remove(commentId);
    }
}