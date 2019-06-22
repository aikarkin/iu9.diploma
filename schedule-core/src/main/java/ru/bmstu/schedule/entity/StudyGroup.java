package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "study_group")
public class StudyGroup {

    private int id;
    private int number;
    private Integer studentsCount;
    private Set<ScheduleDay> scheduleDays = new HashSet<>();
    private StudyPlan studyPlan;
    private Term term;

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
    @Column(name = "group_number", nullable = false)
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Basic
    @Column(name = "students_count")
    public Integer getStudentsCount() {
        return studentsCount;
    }

    public void setStudentsCount(Integer studentsCount) {
        this.studentsCount = studentsCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudyGroup that = (StudyGroup) o;
        return id == that.id &&
                number == that.number &&
                Objects.equals(studentsCount, that.studentsCount) &&
                term.equals(that.term) &&
                studyPlan.equals(that.studyPlan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, studentsCount, studyPlan, term);
    }

    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Set<ScheduleDay> getScheduleDays() {
        return scheduleDays;
    }

    public void setScheduleDays(Set<ScheduleDay> scheduleDays) {
        this.scheduleDays = scheduleDays;
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
    @JoinColumn(name = "term_id", referencedColumnName = "id")
    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

}
