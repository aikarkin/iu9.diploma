package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "specialization")
public class Specialization {

    private int id;
    private Speciality speciality;
    private int numberInSpeciality;
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

    @ManyToOne
    @JoinColumn(name = "speciality_id", referencedColumnName = "id")
    public Speciality getSpeciality() {
        return speciality;
    }

    public void setSpeciality(Speciality speciality) {
        this.speciality = speciality;
    }

    @Column(name = "number_in_speciality", nullable = false)
    public int getNumberInSpeciality() {
        return numberInSpeciality;
    }

    public void setNumberInSpeciality(int numberInSpeciality) {
        this.numberInSpeciality = numberInSpeciality;
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
        Specialization that = (Specialization) o;
        return id == that.id &&
                numberInSpeciality == that.numberInSpeciality &&
                Objects.equals(speciality, that.speciality) &&
                Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, speciality, numberInSpeciality, title);
    }

}
