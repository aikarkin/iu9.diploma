package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
public class Term {
    private int id;
    private int number;
    private Collection<StudyPlanItemCell> studyPlanItemCells;
    private Collection<StudyGroup> studyGroups;

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
    @Column(name = "term_no", nullable = false)
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Term term = (Term) o;
        return id == term.id &&
                number == term.number;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, number);
    }

    @OneToMany(mappedBy = "term")
    public Collection<StudyPlanItemCell> getStudyPlanItemCells() {
        return studyPlanItemCells;
    }

    public void setStudyPlanItemCells(Collection<StudyPlanItemCell> studyPlanItemCells) {
        this.studyPlanItemCells = studyPlanItemCells;
    }

    @OneToMany(mappedBy = "term")
    public Collection<StudyGroup> getStudyGroups() {
        return studyGroups;
    }

    public void setStudyGroups(Collection<StudyGroup> studyGroups) {
        this.studyGroups = studyGroups;
    }
}
