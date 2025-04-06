package ru.relex.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Ad {

    private int prise;
    private String description;

    @Id
    private String title;
    private int id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ad that = (Ad) o;
        return Double.compare(prise, that.prise) == 0 && id == that.id && Objects.equals(title, that.title);
    }
    @Override
    public int hashCode() {
        return Objects.hash(title, prise, id);
    }
    @Override
    public String toString() {
        return "Ad{" +
                "title='" + title + '\'' +
                ", prise=" + prise +
                ", id=" + id +
                '}';
    }
}

