package com.github.spjavaind300.notificationservice.service.imp;

import com.github.spjavaind300.notificationservice.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImp implements CommentService {
    @Override
    public List<Integer> getCommentsAuthorIdsOfAdv(Integer advId) {
        return List.of();
    }
}
