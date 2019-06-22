package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "study_plan")
public class StudyPlan {

    private int id;
    private int startYear;
    private DepartmentSpecialization departmentSpecialization;
    private Set<StudyPlanItem> studyPlanItems = new HashSet<>();
    private Set<StudyGroup> studyGroups = new HashSet<>();

    public StudyPlan() {
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

    @Column(name = "start_year", nullable = false)
    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    @ManyToOne
    @JoinColumn(name = "department_specialization_id", referencedColumnName = "id")
    public DepartmentSpecialization getDepartmentSpecialization() {
        return departmentSpecialization;
    }

    public void setDepartmentSpecialization(DepartmentSpecialization departmentSpecialization) {
        this.departmentSpecialization = departmentSpecialization;
    }

    @OneToMany(mappedBy = "studyPlan", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public Set<StudyPlanItem> getStudyPlanItems() {
        return studyPlanItems;
    }

    public void setStudyPlanItems(Set<StudyPlanItem> studyPlanItems) {
        this.studyPlanItems = studyPlanItems;
    }

    @OneToMany(mappedBy = "studyPlan", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public Set<StudyGroup> getStudyGroups() {
        return studyGroups;
    }

    public void setStudyGroups(Set<StudyGroup> studyGroups) {
        this.studyGroups = studyGroups;
    }

    public void addCalendarItem(StudyPlanItem item) {
        item.setStudyPlan(this);
        getStudyPlanItems().add(item);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudyPlan studyPlan = (StudyPlan) o;
        return id == studyPlan.id &&
                startYear == studyPlan.startYear &&
                Objects.equals(departmentSpecialization, studyPlan.departmentSpecialization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startYear, departmentSpecialization);
    }

}
