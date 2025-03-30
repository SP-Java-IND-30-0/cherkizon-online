package model;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Entity
public class Announcement {

    private String title;
    @NonNull
    private double prise;
    private long id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Announcement(String title, double prise, long id) {
        this.title = title;
        this.prise = prise;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Announcement that = (Announcement) o;
        return Double.compare(prise, that.prise) == 0 && id == that.id && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, prise, id);
    }

    @Override
    public String toString() {
        return "Announcement{" +
                "title='" + title + '\'' +
                ", prise=" + prise +
                ", id=" + id +
                '}';
    }
}
