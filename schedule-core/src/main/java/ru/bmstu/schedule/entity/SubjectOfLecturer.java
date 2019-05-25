package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "lecturer_to_subject")
public class SubjectOfLecturer {

    private int id;
    private Subject subject;
    private Lecturer lecturer;

    public SubjectOfLecturer() {
    }

    public SubjectOfLecturer(Subject subject, Lecturer lecturer) {
        this.subject = subject;
        this.lecturer = lecturer;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "subject_id")
    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "lecturer_id")
    public Lecturer getLecturer() {
        return lecturer;
    }

    public void setLecturer(Lecturer lecturer) {
        this.lecturer = lecturer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubjectOfLecturer that = (SubjectOfLecturer) o;
        return id == that.id &&
                Objects.equals(subject, that.subject) &&
                Objects.equals(lecturer, that.lecturer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, subject, lecturer);
    }

}
