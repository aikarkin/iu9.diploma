package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "department_to_specialization")
public class DepartmentSpecialization {

    private int id;

    private Department department;

    private Specialization specialization;

//    private Set<Calendar> calendarSet = new HashSet<>();

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
    @JoinColumn(name = "department_id")
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "specialization_id")
    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

//    @OneToMany(mappedBy = "departmentSpecialization", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    public Set<Calendar> getCalendarSet() {
//        return calendarSet;
//    }
//
//    public void setCalendarSet(Set<Calendar> calendarSet) {
//        this.calendarSet = calendarSet;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepartmentSpecialization that = (DepartmentSpecialization) o;
        return id == that.id &&
                Objects.equals(department, that.department) &&
                Objects.equals(specialization, that.specialization);
//                Objects.equals(calendarSet, that.calendarSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, department, specialization);
    }

}
