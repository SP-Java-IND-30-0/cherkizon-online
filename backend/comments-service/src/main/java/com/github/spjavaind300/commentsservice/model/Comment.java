package com.github.spjavaind300.commentsservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Entity representing a comment on an advertisement.
 * Each comment is associated with an ad and an author, and contains textual content and a creation timestamp.
 */
@Entity
@Table(name = "comments")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    /**
     * Unique identifier for the comment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;

    /**
     * ID of the user (author) who created the comment.
     */
    @NotNull
    private long authorId;

    /**
     * ID of the advertisement to which this comment belongs.
     */
    @NotNull
    private int adId;

    /**
     * Text content of the comment.
     */
    @NotNull
    private String text;

    /**
     * Timestamp indicating when the comment was created.
     */
    @NotNull
    private Instant createdAt;

    /**
     * Lifecycle callback to set the creation timestamp before the entity is persisted.
     * Ensures {@code createdAt} is not null.
     */
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }
}