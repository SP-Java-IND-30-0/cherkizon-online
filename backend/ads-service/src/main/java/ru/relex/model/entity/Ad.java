package ru.relex.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ads", schema = "app", indexes = {@Index(name = "idx_ads_user_id", columnList = "user_id")})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @EqualsAndHashCode.Include
    int id;

    @Column(name = "title", nullable = false,  length = 32)
    String title;

    @Column(name = "price", nullable = false)
    int price;

    @Column(name = "description", nullable = false, length = 64)
    String description;

    @Column(name = "user_id", nullable = false)
    long userId;

    @Column(name = "image_url", nullable = false, length = 256)
    String imageUrl;

    @Column(name = "original_image_filename", nullable = false, length = 256)
    String originalFilename;

    @Column(name = "image_size", nullable = false)
    int imageSize;

    @Column(name = "image_type", nullable = false, length = 32)
    String imageType;

}

