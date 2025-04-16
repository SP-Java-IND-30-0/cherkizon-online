package com.github.spjavaind300.commentsservice.service;

import com.github.spjavaind300.commentsservice.cache.ProfileCacheService;
import com.github.spjavaind300.commentsservice.feing.AdsFeignClientInternal;
import com.github.spjavaind300.commentsservice.dto.CommentDto;
import com.github.spjavaind300.commentsservice.dto.ProfileDto;
import com.github.spjavaind300.commentsservice.dto.Role;
import com.github.spjavaind300.commentsservice.dto.CommentTextDto;
import com.github.spjavaind300.commentsservice.dto.UserContext;
import com.github.spjavaind300.commentsservice.exception.ForbiddenException;
import com.github.spjavaind300.commentsservice.exception.NotFoundException;
import com.github.spjavaind300.commentsservice.mapper.CommentMapper;
import com.github.spjavaind300.commentsservice.model.Comment;
import com.github.spjavaind300.commentsservice.repository.CommentRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final AdsFeignClientInternal adsFeignClientInternal;
    private final JwtUtils jwtUtils;
    private final HttpServletRequest request;
    private final ProfileCacheService profileCacheService;

    @Override
    public List<CommentDto> getCommentsForAd(int adId) {
        List<Comment> comments = commentRepository.findByAdId(adId);

        if (comments.isEmpty()) {
            throw new NotFoundException("Объявление", adId);
        }

        return comments.stream()
                .map(comment -> {
                    ProfileDto profile = profileCacheService.getProfile(comment.getAuthorId());
                    return commentMapper.toDto(comment, profile);
                })
                .collect(Collectors.toList());
    }

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

        return commentMapper.toDto(savedComment, currentProfile);
    }

    @Override
    public CommentDto updateComment(int adId, int commentId, CommentTextDto commentTextDto) {
        Comment comment = commentRepository.findByIdAndAdId(commentId, adId)
                .orElseThrow(() -> new NotFoundException("Комментарий", commentId));

        validateCommentAccessRights(comment);

        ProfileDto currentProfile = getCurrentProfile();

        comment.setText(commentTextDto.getText());
        Comment updatedComment = commentRepository.save(comment);

        return commentMapper.toDto(updatedComment, currentProfile);
    }

    @Override
    public void deleteComment(int adId, int commentId) {
        Comment comment = commentRepository.findByIdAndAdId(commentId, adId)
                .orElseThrow(() -> new NotFoundException("Комментарий", commentId));

        validateCommentAccessRights(comment);

        commentRepository.delete(comment);
    }

    private void validateCommentAccessRights(Comment comment) {
        UserContext currentUser = jwtUtils.getUserContext(request);

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
        UserContext userContext = jwtUtils.getUserContext(request);
        if (userContext == null) {
            throw new NotFoundException("Пользователь", "не найден в контексте");
        }

        long authorId = userContext.getAuthorId();
        return profileCacheService.getProfile(authorId);
    }
}