package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "department_subject")
public class DepartmentSubject {

    private int id;
    private Department department;
    private Subject subject;
    private Set<LecturerSubject> lecturerSubjects = new HashSet<>();

    public DepartmentSubject() {
    }

    public DepartmentSubject(Department department, Subject subject) {
        this.department = department;
        this.subject = subject;
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

    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "department_id")
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @ManyToOne
    @JoinColumn(name = "subject_id", referencedColumnName = "subject_id")
    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    @OneToMany(mappedBy = "departmentSubject", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public Set<LecturerSubject> getLecturerSubjects() {
        return lecturerSubjects;
    }

    public void setLecturerSubjects(Set<LecturerSubject> lecturerSubjects) {
        this.lecturerSubjects = lecturerSubjects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepartmentSubject that = (DepartmentSubject) o;
        return id == that.id &&
                Objects.equals(department, that.department) &&
                Objects.equals(subject, that.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, department, subject);
    }

}
