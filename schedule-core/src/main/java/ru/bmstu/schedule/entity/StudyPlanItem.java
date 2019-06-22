package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "study_plan_item")
public class StudyPlanItem {

    private int id;
    private StudyPlan studyPlan;
    private DepartmentSubject departmentSubject;
    private Set<StudyPlanItemCell> studyPlanItemCells = new HashSet<>();

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "study_plan_id", referencedColumnName = "id")
    public StudyPlan getStudyPlan() {
        return studyPlan;
    }

    public void setStudyPlan(StudyPlan studyPlan) {
        this.studyPlan = studyPlan;
    }

    @ManyToOne
    @JoinColumn(name = "department_subject_id", referencedColumnName = "id")
    public DepartmentSubject getDepartmentSubject() {
        return departmentSubject;
    }

    public void setDepartmentSubject(DepartmentSubject subject) {
        this.departmentSubject = subject;
    }

    @OneToMany(mappedBy = "studyPlanItem", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public Set<StudyPlanItemCell> getStudyPlanItemCells() {
        return studyPlanItemCells;
    }

    public void setStudyPlanItemCells(Set<StudyPlanItemCell> studyPlanItemCells) {
        this.studyPlanItemCells = studyPlanItemCells;
    }

    public void addItemCell(StudyPlanItemCell cell) {
        cell.setStudyPlanItem(this);
        this.getStudyPlanItemCells().add(cell);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudyPlanItem that = (StudyPlanItem) o;
        return id == that.id &&
                Objects.equals(studyPlan, that.studyPlan) &&
                Objects.equals(departmentSubject, that.departmentSubject) &&
                Objects.equals(studyPlanItemCells, that.studyPlanItemCells);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, studyPlan, departmentSubject);
    }

}
