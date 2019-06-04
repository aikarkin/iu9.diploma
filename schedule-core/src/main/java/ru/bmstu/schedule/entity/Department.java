package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "department")
public class Department {

    private int id;
    private int number;
    private String title;
    private Faculty faculty;

    private Set<DepartmentSpecialization> departmentSpecializations = new HashSet<>();

    public Department(int number, String title) {
        this.number = number;
        this.title = title;
    }

    public Department() {

    }

    public Department(int number, String title, Faculty faculty) {
        this.number = number;
        this.title = title;
        this.faculty = faculty;
    }

    @Id
    @Column(name = "department_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "department_number", nullable = false)
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Transient
    public String getCipher() {
        return String.format("%s%d", getFaculty().getCipher(), getNumber());
    }

    @Basic
    @Column(name = "title", length = -1)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ManyToOne
    @JoinColumn(name = "faculty_id", referencedColumnName = "faculty_id")
    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public void addSpecialization(Specialization spec) {
        DepartmentSpecialization depSpec = new DepartmentSpecialization();
        depSpec.setSpecialization(spec);
        depSpec.setDepartment(this);
        getDepartmentSpecializations().add(depSpec);
    }

    @Transient
    public List<Specialization> getSpecializations() {
        return getDepartmentSpecializations()
                .stream()
                .map(DepartmentSpecialization::getSpecialization)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", number=" + number +
                ", title='" + title + '\'' +
                (faculty == null ? "" : ", faculty=" + faculty.getCipher()) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return id == that.id &&
                number == that.number &&
                Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, title);
    }

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Set<DepartmentSpecialization> getDepartmentSpecializations() {
        return departmentSpecializations;
    }

    void setDepartmentSpecializations(Set<DepartmentSpecialization> departmentSpecializations) {
        this.departmentSpecializations = departmentSpecializations;
    }

}
