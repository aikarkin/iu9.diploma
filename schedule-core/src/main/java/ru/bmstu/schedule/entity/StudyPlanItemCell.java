package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "study_plan_item_cell")
public class StudyPlanItemCell {
    private int id;
    private StudyPlanItem studyPlanItem;
    private Term term;
    private Set<HoursPerClass> hoursPerClasses = new HashSet<>();

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudyPlanItemCell that = (StudyPlanItemCell) o;
        return id == that.id &&
                Objects.equals(studyPlanItem.getId(), that.studyPlanItem.getId()) &&
                Objects.equals(term.getId(), that.term.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, studyPlanItem.getId(), term.getId());
    }

    @ManyToOne
    @JoinColumn(name = "study_plan_item_id", referencedColumnName = "id")
    public StudyPlanItem getStudyPlanItem() {
        return studyPlanItem;
    }

    public void setStudyPlanItem(StudyPlanItem studyPlanItem) {
        this.studyPlanItem = studyPlanItem;
    }

    @ManyToOne
    @JoinColumn(name = "term_id", referencedColumnName = "id")
    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    @OneToMany(mappedBy = "studyPlanItemCell", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public Set<HoursPerClass> getHoursPerClasses() {
        return hoursPerClasses;
    }

    public void setHoursPerClasses(Set<HoursPerClass> hoursPerClasses) {
        this.hoursPerClasses = hoursPerClasses;
    }

    public void addHoursPerClass(HoursPerClass hpc) {
        hpc.setStudyPlanItemCell(this);
        this.getHoursPerClasses().add(hpc);
    }
}
