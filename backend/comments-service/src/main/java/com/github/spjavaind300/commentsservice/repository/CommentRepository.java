package com.github.spjavaind300.commentsservice.repository;

import com.github.spjavaind300.commentsservice.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    Optional<Comment> findByIdAndAdId(int commentId, int adId);

    List<Comment> findByAdId(int adId);
}