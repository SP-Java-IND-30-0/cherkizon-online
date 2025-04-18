package com.github.spjavaind300.commentsservice.repository;

import com.github.spjavaind300.commentsservice.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    Optional<Comment> findByIdAndAdId(int commentId, int adId);

    List<Comment> findByAdId(int adId);

    @Query("SELECT DISTINCT c.authorId FROM Comment c WHERE c.adId = :adId")
    Set<Long> findAuthorIdsByAdId(@Param("adId") int adId);

    void deleteAllByAuthorId(long authorId);

    void deleteAllByAdId(int adId);
}