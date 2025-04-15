package com.github.spjavaind300.commentsservice.repository;

import com.github.spjavaind300.commentsservice.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    Optional<Comment> findByIdAndAdId(int commentId, int adId);

    Page<Comment> findByAdId(int adId, Pageable pageable);

    boolean existsByAdId(int adId);
}