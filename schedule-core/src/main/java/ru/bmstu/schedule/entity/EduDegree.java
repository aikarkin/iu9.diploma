package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "edu_degree")
public class EduDegree {

    private int id;
    private String name;
    private int minNumberOfStudyYears;
    private Collection<Speciality> specialities;

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
    @Column(name = "degree_name", nullable = false, length = -1)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "min_number_of_study_years", nullable = false)
    public int getMinNumberOfStudyYears() {
        return minNumberOfStudyYears;
    }

    public void setMinNumberOfStudyYears(int minNumberOfStudyYears) {
        this.minNumberOfStudyYears = minNumberOfStudyYears;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EduDegree degree = (EduDegree) o;
        return id == degree.id &&
                minNumberOfStudyYears == degree.minNumberOfStudyYears &&
                Objects.equals(name, degree.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, minNumberOfStudyYears);
    }

    @OneToMany(mappedBy = "degree", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public Collection<Speciality> getSpecialities() {
        return specialities;
    }

    public void setSpecialities(Collection<Speciality> specializations) {
        this.specialities = specializations;
    }

}
