package com.github.spjavaind300.commentsservice.mapper;

import com.github.spjavaind300.commentsservice.dto.CommentDto;
import com.github.spjavaind300.commentsservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface CommentMapper {

    @Mappings({
            @Mapping(target = "author", source = "authorId"),
            @Mapping(target = "authorFirstName", source = "authorFirstName"),
            @Mapping(target = "authorImage", source = "authorImage"),
            @Mapping(target = "id", source = "comment.id"),
            @Mapping(target = "text", source = "comment.text"),
            @Mapping(target = "createdAt", source = "comment.createdAt")
    })
    CommentDto toDto(Comment comment, long authorId, String authorFirstName, String authorImage);
}