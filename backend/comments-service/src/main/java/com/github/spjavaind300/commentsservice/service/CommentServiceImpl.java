package com.github.spjavaind300.commentsservice.service;

import com.github.spjavaind300.commentsservice.cache.ProfileCacheService;
import com.github.spjavaind300.commentsservice.kafka.dto.CommentCreatedEvent;
import com.github.spjavaind300.commentsservice.exception.UnauthorizedException;
import com.github.spjavaind300.commentsservice.feing.AdsFeignClientInternal;
import com.github.spjavaind300.commentsservice.dto.CommentDto;
import com.github.spjavaind300.commentsservice.dto.ProfileDto;
import com.github.spjavaind300.commentsservice.dto.Role;
import com.github.spjavaind300.commentsservice.dto.CommentTextDto;
import com.github.spjavaind300.commentsservice.dto.UserContext;
import com.github.spjavaind300.commentsservice.exception.ForbiddenException;
import com.github.spjavaind300.commentsservice.exception.NotFoundException;
import com.github.spjavaind300.commentsservice.kafka.CommentKafkaProducer;
import com.github.spjavaind300.commentsservice.mapper.CommentMapper;
import com.github.spjavaind300.commentsservice.model.Comment;
import com.github.spjavaind300.commentsservice.repository.CommentRepository;
import com.github.spjavaind300.commentsservice.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link CommentService} that handles operations related to comments on ads.
 * This service is responsible for creating, retrieving, updating, and deleting comments, as well as publishing
 * comment-related events to Kafka.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final AdsFeignClientInternal adsFeignClientInternal;
    private final JwtUtils jwtUtils;
    private final ProfileCacheService profileCacheService;
    private final CommentKafkaProducer commentKafkaProducer;

    /**
     * Retrieves all comments associated with a specific advertisement.
     *
     * @param adId the ID of the advertisement
     * @return a list of {@link CommentDto} containing comments for the specified ad
     */
    @Override
    public List<CommentDto> getCommentsForAd(int adId) {
        List<Comment> comments = commentRepository.findByAdId(adId);
        log.info("Найдено {} комментариев для объявления с id: {}", comments.size(), adId);

        return comments.stream()
                .map(comment -> {
                    ProfileDto profile = profileCacheService.getProfile(comment.getAuthorId());
                    return commentMapper.toDto(comment, profile);
                })
                .collect(Collectors.toList());
    }

    /**
     * Adds a new comment to the specified advertisement.
     * Sends a Kafka event upon successful creation.
     *
     * @param adId           the ID of the advertisement
     * @param commentTextDto the DTO containing the text of the comment
     * @return the created {@link CommentDto}
     * @throws NotFoundException if the advertisement does not exist
     */
    @Transactional
    @Override
    public CommentDto addComment(int adId, CommentTextDto commentTextDto) {
        adsFeignClientInternal.checkAdExists(adId);

        ProfileDto currentProfile = getCurrentProfile();

        Comment comment = Comment.builder()
                .adId(adId)
                .text(commentTextDto.getText())
                .authorId(currentProfile.getAuthorId())
                .createdAt(Instant.now())
                .build();

        Comment savedComment = commentRepository.save(comment);

        log.info("Комментарий с id: {} был сохранен для объявления с id: {}.",
                savedComment.getId(), adId);

        CommentCreatedEvent event = new CommentCreatedEvent(
                savedComment.getId(),
                adId,
                savedComment.getAuthorId(),
                savedComment.getText(),
                savedComment.getCreatedAt()
        );

        try {
            log.info("Отправляем событие в Kafka: {}", event);
            commentKafkaProducer.sendCommentCreatedEvent(event);
        } catch (Exception e) {
            log.error("Ошибка при отправке события в Kafka для комментария с id: {}", savedComment.getId(), e);
        }

        return commentMapper.toDto(savedComment, currentProfile);
    }

    /**
     * Updates an existing comment for a given advertisement.
     *
     * @param adId           the ID of the advertisement
     * @param commentId      the ID of the comment to update
     * @param commentTextDto the DTO containing updated comment text
     * @return the updated {@link CommentDto}
     * @throws NotFoundException if the comment is not found
     * @throws ForbiddenException if the current user is not the comment's author or lacks sufficient rights
     */
    @Override
    public CommentDto updateComment(int adId, int commentId, CommentTextDto commentTextDto) {
        Comment comment = commentRepository.findByIdAndAdId(commentId, adId)
                .orElseThrow(() -> new NotFoundException("Комментарий", commentId));

        validateCommentAccessRights(comment);

        ProfileDto currentProfile = getCurrentProfile();

        comment.setText(commentTextDto.getText());
        Comment updatedComment = commentRepository.save(comment);
        log.info("Комментарий с id: {} был обновлен для объявления с id: {}", updatedComment.getId(), adId);

        return commentMapper.toDto(updatedComment, currentProfile);
    }

    /**
     * Deletes a comment by its ID and associated advertisement ID.
     *
     * @param adId      the ID of the advertisement
     * @param commentId the ID of the comment to delete
     * @throws NotFoundException if the comment is not found
     * @throws ForbiddenException if the current user is not the comment's author or lacks sufficient rights
     */
    @Override
    public void deleteComment(int adId, int commentId) {
        Comment comment = commentRepository.findByIdAndAdId(commentId, adId)
                .orElseThrow(() -> new NotFoundException("Комментарий", commentId));

        validateCommentAccessRights(comment);
        commentRepository.delete(comment);
        log.info("Комментарий с id: {} был удален для объявления с id: {}", commentId, adId);
    }

    /**
     * Retrieves the unique author IDs of all comments associated with a given advertisement.
     *
     * @param adId the ID of the advertisement
     * @return a set of unique author IDs
     */
    @Override
    public Set<Long> getAuthorIdsByAdId(int adId) {
        return commentRepository.findAuthorIdsByAdId(adId);
    }

    /**
     * Deletes all comments authored by the specified user.
     *
     * @param authorId the ID of the comment author
     */
    @Transactional
    @Override
    public void deleteCommentsByAuthorId(long authorId) {
        commentRepository.deleteAllByAuthorId(authorId);
        log.info("Deleted all comments for authorId={}", authorId);
    }

    /**
     * Deletes all comments associated with a specific advertisement.
     *
     * @param adId the ID of the advertisement
     */
    @Transactional
    @Override
    public void deleteCommentsByAdId(int adId) {
        commentRepository.deleteAllByAdId(adId);
        log.info("Deleted all comments for adId={}", adId);
    }

    private void validateCommentAccessRights(Comment comment) {
        UserContext currentUser = jwtUtils.getUserContext();

        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.SERVICE) {
            return;
        }

        if (comment.getAuthorId() != currentUser.getAuthorId()) {
            throw new ForbiddenException(
                    currentUser.getAuthorId(),
                    currentUser.getRole(),
                    comment.getAuthorId(),
                    comment.getId()
            );
        }
    }

    private ProfileDto getCurrentProfile() {
        UserContext userContext = jwtUtils.getUserContext();
        if (userContext == null) {
            throw new UnauthorizedException();
        }

        long authorId = userContext.getAuthorId();
        return profileCacheService.getProfile(authorId);
    }
}