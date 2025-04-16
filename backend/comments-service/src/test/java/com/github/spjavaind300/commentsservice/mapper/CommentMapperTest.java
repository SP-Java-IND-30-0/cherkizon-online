package com.github.spjavaind300.commentsservice.mapper;

import com.github.spjavaind300.commentsservice.dto.CommentDto;
import com.github.spjavaind300.commentsservice.dto.ProfileDto;
import com.github.spjavaind300.commentsservice.model.Comment;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

class CommentMapperTest {

    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void shouldMapCommentToDtoWithProfile() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setAdId(100);
        comment.setText("Test comment");
        comment.setCreatedAt(Instant.parse("2025-04-12T12:00:00Z"));
        comment.setAuthorId(42L);

        ProfileDto profileDto = new ProfileDto();
        profileDto.setAuthorId(42L);
        profileDto.setAuthorFirstName("Alice");
        profileDto.setAuthorImage("image-url");

        CommentDto dto = commentMapper.toDto(comment, profileDto);

        System.out.println(dto);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getText()).isEqualTo("Test comment");
        assertThat(dto.getCreatedAt()).isEqualTo(Instant.parse("2025-04-12T12:00:00Z"));
        assertThat(dto.getAuthorId()).isEqualTo(42L);
        assertThat(dto.getAuthorFirstName()).isEqualTo("Alice");
        assertThat(dto.getAuthorImage()).isEqualTo("image-url");
    }
}