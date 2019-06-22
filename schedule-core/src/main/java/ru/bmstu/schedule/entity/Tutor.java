package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tutor")
public class Tutor {

    private int id;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String scienceDegree;

    @Override
    public String toString() {
        return "Lecturer{" +
                "email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", degree='" + scienceDegree + '\'' +
                '}';
    }

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "email", length = -1)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "first_name", nullable = false, length = -1)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Basic
    @Column(name = "middle_name", nullable = false, length = -1)
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @Basic
    @Column(name = "last_name", nullable = false, length = -1)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Basic
    @Column(name = "science_degree", length = -1)
    public String getScienceDegree() {
        return scienceDegree;
    }

    public void setScienceDegree(String eduDegree) {
        this.scienceDegree = eduDegree;
    }

    @Transient
    public String getInitials() {
        return String.format("%s %s. %s.", getLastName(), firstUpperLetter(getFirstName()), firstUpperLetter(getMiddleName()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tutor tutor = (Tutor) o;
        return id == tutor.id &&
                Objects.equals(email, tutor.email) &&
                Objects.equals(firstName, tutor.firstName) &&
                Objects.equals(middleName, tutor.middleName) &&
                Objects.equals(lastName, tutor.lastName) &&
                Objects.equals(scienceDegree, tutor.scienceDegree);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, firstName, middleName, lastName, scienceDegree);
    }

    private static char firstUpperLetter(String name) {
        return name.toUpperCase().charAt(0);
    }

}
