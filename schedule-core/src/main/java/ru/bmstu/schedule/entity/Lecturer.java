package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Lecturer {
    private int id;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String eduDegree;

    @Override
    public String toString() {
        return "Lecturer{" +
                "email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", eduDegree='" + eduDegree + '\'' +
                '}';
    }

    private Set<ScheduleItemParity> scheduleItemParities = new HashSet<>();

    @Id
    @Column(name = "lecturer_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "lecturer_email", nullable = true, length = -1)
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
    @Column(name = "edu_degree", nullable = true, length = -1)
    public String getEduDegree() {
        return eduDegree;
    }

    public void setEduDegree(String eduDegree) {
        this.eduDegree = eduDegree;
    }

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinTable(
            name="schedule_item_parity_to_lecturer",
            joinColumns = { @JoinColumn(name="lecturer_id") },
            inverseJoinColumns = { @JoinColumn(name="schedule_item_parity_id") }
    )
    public Set<ScheduleItemParity> getScheduleItemParities() {
        return scheduleItemParities;
    }

    void setScheduleItemParities(Set<ScheduleItemParity> scheduleItemParities) {
        this.scheduleItemParities = scheduleItemParities;
    }

    void addScheduleItemParity(ScheduleItemParity itemParity) {
        itemParity.getLecturers().add(this);
        getScheduleItemParities().add(itemParity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lecturer lecturer = (Lecturer) o;
        return id == lecturer.id &&
                Objects.equals(email, lecturer.email) &&
                Objects.equals(firstName, lecturer.firstName) &&
                Objects.equals(middleName, lecturer.middleName) &&
                Objects.equals(lastName, lecturer.lastName) &&
                Objects.equals(eduDegree, lecturer.eduDegree);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, email, firstName, middleName, lastName, eduDegree);
    }
}
