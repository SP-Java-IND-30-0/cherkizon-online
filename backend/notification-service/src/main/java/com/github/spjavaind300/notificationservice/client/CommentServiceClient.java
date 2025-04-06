package com.github.spjavaind300.notificationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Set;

@FeignClient(name = "comments-service")
public interface CommentServiceClient {

    @GetMapping("/internal/comment/{id}")
    Set<Integer> getCommentAuthorIds(@PathVariable("id") int id);
}
