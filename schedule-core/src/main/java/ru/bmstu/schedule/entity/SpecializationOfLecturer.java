package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "lecturer_to_specialization")
public class SpecializationOfLecturer {

    private int id;
    private Lecturer lecturer;
    private Specialization specialization;

    public SpecializationOfLecturer() {
    }

    public SpecializationOfLecturer(Lecturer lecturer, Specialization specialization) {
        this.lecturer = lecturer;
        this.specialization = specialization;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "lecturer_id")
    public Lecturer getLecturer() {
        return lecturer;
    }

    public void setLecturer(Lecturer lecturer) {
        this.lecturer = lecturer;
    }

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "spec_id")
    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpecializationOfLecturer that = (SpecializationOfLecturer) o;
        return id == that.id &&
                Objects.equals(lecturer, that.lecturer) &&
                Objects.equals(specialization, that.specialization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lecturer, specialization);
    }

}
