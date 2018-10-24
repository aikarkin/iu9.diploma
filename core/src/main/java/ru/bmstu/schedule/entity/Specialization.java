package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
public class Specialization {
    private int id;
    private String code;
    private String title;
    private Collection<Department> departments;
    private EduDegree eduDegree;
    private Collection<StudyFlow> studyFlows;

    @Id
    @Column(name = "spec_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "spec_code", columnDefinition = "bpchar", length = 8)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "title", nullable = false, length = -1)
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
                Objects.equals(code, that.code) &&
                Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, code, title);
    }

    @ManyToMany(mappedBy = "specializations")
    public Collection<Department> getDepartments() {
        return departments;
    }

    void setDepartments(Collection<Department> departments) {
        this.departments = departments;
    }

    public void addDepartment(Department dep) {
        this.getDepartments().add(dep);
        dep.getSpecializations().add(this);
    }

    @ManyToOne
    @JoinColumn(name = "degree_id", referencedColumnName = "degree_id")
    public EduDegree getEduDegree() {
        return eduDegree;
    }

    public void setEduDegree(EduDegree eduDegree) {
        this.eduDegree = eduDegree;
    }

    @OneToMany(mappedBy = "specialization")
    public Collection<StudyFlow> getStudyFlows() {
        return studyFlows;
    }

    public void setStudyFlows(Collection<StudyFlow> studyFlows) {
        this.studyFlows = studyFlows;
    }
}
