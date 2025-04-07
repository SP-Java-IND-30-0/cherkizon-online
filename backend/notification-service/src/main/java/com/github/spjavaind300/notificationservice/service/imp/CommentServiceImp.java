package com.github.spjavaind300.notificationservice.service.imp;

import com.github.spjavaind300.notificationservice.client.CommentServiceClient;
import com.github.spjavaind300.notificationservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImp implements CommentService {

    private final CommentServiceClient client;

    @Override
    public List<Long> getCommentsAuthorIdsOfAdv(long advId) {

        return new ArrayList<>(client.getCommentAuthorIds(advId));
    }
}
