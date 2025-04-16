package com.github.spjavaind300.commentsservice.mapper;

import com.github.spjavaind300.commentsservice.dto.CommentDto;
import com.github.spjavaind300.commentsservice.dto.ProfileDto;
import com.github.spjavaind300.commentsservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "authorId", source = "comment.authorId")
    @Mapping(target = "authorFirstName", source = "profileDto.authorFirstName")
    @Mapping(target = "authorImage", source = "profileDto.authorImage")
    CommentDto toDto(Comment comment, ProfileDto profileDto);
}