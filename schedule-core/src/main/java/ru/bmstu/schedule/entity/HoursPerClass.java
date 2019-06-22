package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "hours_per_class")
public class HoursPerClass {
    private int id;
    private int noOfHours;
    private StudyPlanItemCell studyPlanItemCell;
    private ClassType classType;

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
    @Column(name = "no_of_hours", nullable = false)
    public int getNoOfHours() {
        return noOfHours;
    }

    public void setNoOfHours(int noOfHours) {
        this.noOfHours = noOfHours;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoursPerClass that = (HoursPerClass) o;
        return id == that.id &&
                classType.getName().equals(((HoursPerClass) o).classType.getName()) &&
                noOfHours == that.noOfHours;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, classType.getName(), noOfHours);
    }

    @ManyToOne
    @JoinColumn(name = "study_plan_item_cell_id", referencedColumnName = "id")
    public StudyPlanItemCell getStudyPlanItemCell() {
        return studyPlanItemCell;
    }

    public void setStudyPlanItemCell(StudyPlanItemCell studyPlanItemCell) {
        this.studyPlanItemCell = studyPlanItemCell;
    }

    @ManyToOne
    @JoinColumn(name = "class_type_id", referencedColumnName = "id")
    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }
}
