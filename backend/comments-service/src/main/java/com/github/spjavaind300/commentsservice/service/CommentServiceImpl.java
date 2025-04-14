package com.github.spjavaind300.commentsservice.service;

import com.github.spjavaind300.commentsservice.component.CommentProfileCache;
import com.github.spjavaind300.commentsservice.client.ProfileFeignClientInternal;
import com.github.spjavaind300.commentsservice.dto.CommentDto;
import com.github.spjavaind300.commentsservice.dto.ProfileDto;
import com.github.spjavaind300.commentsservice.exception.BadRequestException;
import com.github.spjavaind300.commentsservice.exception.ForbiddenException;
import com.github.spjavaind300.commentsservice.exception.NotFoundException;
import com.github.spjavaind300.commentsservice.mapper.CommentMapper;
import com.github.spjavaind300.commentsservice.model.Comment;
import com.github.spjavaind300.commentsservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ProfileFeignClientInternal profileFeignClient;
    private final CommentMapper commentMapper;
    private final CommentProfileCache commentProfileCache;

    @Override
    public List<CommentDto> getCommentsForAd(int adId) {
        List<Comment> comments = commentRepository.findAllByAdId(adId);
        if (comments.isEmpty()) {
            log.warn("Не найдены комментарии для объявления с id: {}", adId);
        }

        return comments.stream()
                .map(comment -> {
                    ProfileDto profile = commentProfileCache.get(comment.getId());
                    if (profile == null) {
                        throw new NotFoundException(comment.getId());
                    }
                    return commentMapper.toDto(comment, profile);
                })
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(int adId, String text) {
        ProfileDto currentProfile = Optional.ofNullable(profileFeignClient.getCurrentProfileInternal())
                .orElseThrow(BadRequestException::new);

        Comment comment = Comment.builder()
                .adId(adId)
                .text(text)
                .createdAt(Instant.now())
                .build();

        Comment savedComment = commentRepository.save(comment);
        commentProfileCache.put(savedComment.getId(), currentProfile);
        log.info("Профиль автора с id: {} добавлен в кэш для комментария с id: {}", currentProfile.getAuthorId(), savedComment.getId());

        return commentMapper.toDto(savedComment, currentProfile);
    }

    @Override
    public CommentDto updateComment(int adId, int commentId, String newText) {

        Comment comment = findCommentById(commentId);
        validateCommentBelongsToAd(comment, adId);
        ProfileDto currentProfile = getCurrentProfile();
        validateCommentAccessRights(commentId, currentProfile);

        comment.setText(newText);
        Comment updatedComment = commentRepository.save(comment);

        return commentMapper.toDto(updatedComment, currentProfile);
    }

    @Override
    public void deleteComment(int adId, int commentId) {

        Comment comment = findCommentById(commentId);
        validateCommentBelongsToAd(comment, adId);
        ProfileDto currentProfile = getCurrentProfile();
        validateCommentAccessRights(commentId, currentProfile);

        commentRepository.delete(comment);
        commentProfileCache.remove(commentId);
    }

    private void validateCommentBelongsToAd(Comment comment, int adId) {
        if (comment.getAdId() != adId) {
            throw new BadRequestException(comment.getId());
        }
    }

    private void validateCommentAccessRights(int commentId, ProfileDto currentProfile) {
        ProfileDto commentAuthor = commentProfileCache.get(commentId);
        if (commentAuthor == null || commentAuthor.getAuthorId() != currentProfile.getAuthorId()) {
            throw new ForbiddenException(commentId);
        }
    }

    private ProfileDto getCurrentProfile() {
        return Optional.ofNullable(profileFeignClient.getCurrentProfileInternal())
                .orElseThrow(BadRequestException::new);
    }

    private Comment findCommentById(int commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(commentId));
    }
}