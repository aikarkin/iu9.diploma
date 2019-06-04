package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "lecturer_subject")
public class LecturerSubject {

    private int id;
    private Lecturer lecturer;
    private DepartmentSubject departmentSubject;
    private ClassType classType;

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
    @JoinColumn(name = "lecturer_id", referencedColumnName = "lecturer_id")
    public Lecturer getLecturer() {
        return lecturer;
    }

    public void setLecturer(Lecturer lecturer) {
        this.lecturer = lecturer;
    }

    @ManyToOne
    @JoinColumn(name = "subject_on_department_id", referencedColumnName = "id")
    public DepartmentSubject getDepartmentSubject() {
        return departmentSubject;
    }

    public void setDepartmentSubject(DepartmentSubject subject) {
        this.departmentSubject = subject;
    }

    @ManyToOne
    @JoinColumn(name = "class_type_id", referencedColumnName = "type_id")
    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LecturerSubject that = (LecturerSubject) o;
        return id == that.id &&
                Objects.equals(lecturer, that.lecturer) &&
                Objects.equals(departmentSubject, that.departmentSubject) &&
                Objects.equals(classType, that.classType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lecturer, departmentSubject, classType);
    }

}
