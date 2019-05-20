package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "study_group", schema = "public", catalog = "schedule")
public class StudyGroup {
    private int id;
    private int number;
    private Integer studentsCount;
    private Set<ScheduleDay> scheduleDays = new HashSet<>();
    private StudyFlow studyFlow;
    private Term term;

    @Id
    @Column(name = "group_id", nullable = false)
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
    @Column(name = "students_count", nullable = true)
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
                studyFlow.equals(that.studyFlow);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, studentsCount, studyFlow, term);
    }

    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Set<ScheduleDay> getScheduleDays() {
        return scheduleDays;
    }

    public void setScheduleDays(Set<ScheduleDay> scheduleDays) {
        this.scheduleDays = scheduleDays;
    }

    public void addScheduleDay(ScheduleDay day) {
        day.setStudyGroup(this);
        getScheduleDays().add(day);
    }

    @ManyToOne
    @JoinColumn(name = "flow_id", referencedColumnName = "flow_id")
    public StudyFlow getStudyFlow() {
        return studyFlow;
    }

    public void setStudyFlow(StudyFlow studyFlow) {
        this.studyFlow = studyFlow;
    }

    @ManyToOne
    @JoinColumn(name = "term_id", referencedColumnName = "term_id")
    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }
}
