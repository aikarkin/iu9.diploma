package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "speciality")
public class Speciality {

    private int id;
    private String code;
    private EduDegree degree;
    private String title;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "code", nullable = false, unique = true, columnDefinition = "bpchar", length = 8)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @ManyToOne
    @JoinColumn(name = "degree_id", referencedColumnName = "id")
    public EduDegree getDegree() {
        return degree;
    }

    public void setDegree(EduDegree degree) {
        this.degree = degree;
    }

    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Speciality that = (Speciality) o;
        return id == that.id &&
                Objects.equals(code, that.code) &&
                Objects.equals(degree, that.degree) &&
                Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, degree == null ? 0 : degree.getId(), title);
    }

}
