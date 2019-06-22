package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tutor_subject")
public class TutorSubject {

    private int id;
    private Tutor tutor;
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
    @JoinColumn(name = "tutor_id", referencedColumnName = "id")
    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }

    @ManyToOne
    @JoinColumn(name = "department_subject_id", referencedColumnName = "id")
    public DepartmentSubject getDepartmentSubject() {
        return departmentSubject;
    }

    public void setDepartmentSubject(DepartmentSubject subject) {
        this.departmentSubject = subject;
    }

    @ManyToOne
    @JoinColumn(name = "class_type_id", referencedColumnName = "id")
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
        TutorSubject that = (TutorSubject) o;
        return id == that.id &&
                Objects.equals(tutor, that.tutor) &&
                Objects.equals(departmentSubject, that.departmentSubject) &&
                Objects.equals(classType, that.classType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tutor, departmentSubject, classType);
    }

}
