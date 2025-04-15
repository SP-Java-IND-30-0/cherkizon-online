package com.github.spjavaind300.commentsservice.service;

import com.github.spjavaind300.commentsservice.component.AuthorProfileCache;
import com.github.spjavaind300.commentsservice.client.ProfileFeignClientInternal;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ProfileFeignClientInternal profileFeignClient;
    private final CommentMapper commentMapper;
    private final AuthorProfileCache authorProfileCache;
    private final JwtUtils jwtUtils;
    private final HttpServletRequest request;

    @Override
    public Map<String, List<CommentDto>> getCommentsForAd(int adId, int page, int size) {

        if (!commentRepository.existsByAdId(adId)) {
            throw new NotFoundException("Объявление", adId);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findByAdId(adId, pageable);

        List<CommentDto> commentDtos = commentPage.stream()
                .map(comment -> {
                    ProfileDto profile = Optional.ofNullable(authorProfileCache.get(comment.getAuthorId()))
                            .orElseGet(() -> {
                                ProfileDto fetchedProfile = profileFeignClient.getProfileByIdInternal(comment.getAuthorId());
                                authorProfileCache.put(comment.getAuthorId(), fetchedProfile);
                                return fetchedProfile;
                            });
                    return commentMapper.toDto(comment, profile);
                })
                .collect(Collectors.toList());

        Map<String, List<CommentDto>> result = new HashMap<>();
        result.put("results", commentDtos);
        return result;
    }

    @Override
    public CommentDto addComment(int adId, CommentTextDto commentTextDto) {

        if (!commentRepository.existsByAdId(adId)) {
            throw new NotFoundException("Объявление", adId);
        }

        ProfileDto currentProfile = getCurrentProfile();

        Comment comment = Comment.builder()
                .adId(adId)
                .text(commentTextDto.getText())
                .authorId(currentProfile.getAuthorId())
                .createdAt(Instant.now())
                .build();

        Comment savedComment = commentRepository.save(comment);
        authorProfileCache.put(savedComment.getAuthorId(), currentProfile);

        log.info("Профиль автора с id: {} добавлен в кэш для комментария с id: {}",
                currentProfile.getAuthorId(), savedComment.getId());

        return commentMapper.toDto(savedComment, currentProfile);
    }

    @Override
    public CommentDto updateComment(int adId, int commentId, CommentTextDto commentTextDto) {

        Comment comment = commentRepository.findByIdAndAdId(commentId, adId)
                .orElseThrow(() -> new NotFoundException("Комментарий", commentId));

        ProfileDto currentProfile = getCurrentProfile();
        validateCommentAccessRights(comment, currentProfile);

        comment.setText(commentTextDto.getText());
        Comment updatedComment = commentRepository.save(comment);

        return commentMapper.toDto(updatedComment, currentProfile);
    }

    @Override
    public void deleteComment(int adId, int commentId) {

        Comment comment = commentRepository.findByIdAndAdId(commentId, adId)
                .orElseThrow(() -> new NotFoundException("Комментарий", commentId));

        ProfileDto currentProfile = getCurrentProfile();
        validateCommentAccessRights(comment, currentProfile);

        commentRepository.delete(comment);
        authorProfileCache.remove(comment.getAuthorId());
    }

    private void validateCommentAccessRights(Comment comment, ProfileDto currentProfile) {
        UserContext currentUser = jwtUtils.getUserContext(request);

        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.SERVICE) {
            return;
        }

        if (comment.getAuthorId() != currentProfile.getAuthorId()) {
            throw new ForbiddenException(comment.getId());
        }
    }

    private ProfileDto getCurrentProfile() {

        UserContext userContext = jwtUtils.getUserContext(request);
        if (userContext == null) {
            throw new NotFoundException("Пользователь", "не найден в контексте");
        }

        long authorId = userContext.getAuthorId();

        return Optional.ofNullable(authorProfileCache.get(authorId))
                .orElseGet(() -> {
                    ProfileDto profile = profileFeignClient.getProfileByIdInternal(authorId);
                    authorProfileCache.put(authorId, profile);
                    return profile;
                });
    }
}