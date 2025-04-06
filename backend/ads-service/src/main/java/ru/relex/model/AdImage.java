package ru.relex.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Objects;

@Entity
@Getter
@Setter
public class AdImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String filePath;
    private long fileSize;
    private String mediaType;
    @Lob
    private byte[] data;

    @OneToOne
    private Ad ad;

    public AdImage() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdImage adImage = (AdImage) o;
        return id == adImage.id && fileSize == adImage.fileSize && Objects.equals(filePath, adImage.filePath) && Objects.equals(mediaType, adImage.mediaType) && Objects.deepEquals(data, adImage.data) && Objects.equals(ad, adImage.ad);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, filePath, fileSize, mediaType, Arrays.hashCode(data), ad);
    }

    @Override
    public String toString() {
        return "AdImage{" +
                "id=" + id +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", mediaType='" + mediaType + '\'' +
                ", data=" + Arrays.toString(data) +
                ", ad=" + ad +
                '}';
    }
}
