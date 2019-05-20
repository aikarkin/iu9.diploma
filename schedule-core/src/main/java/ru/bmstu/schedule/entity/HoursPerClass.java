package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "hours_per_class", schema = "public", catalog = "schedule")
public class HoursPerClass {
    private int id;
    private int noOfHours;
    private CalendarItemCell calendarItemCell;
    private ClassType classType;

    @Id
    @Column(name = "hours_id", nullable = false)
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
                noOfHours == that.noOfHours;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, noOfHours);
    }

    @ManyToOne
    @JoinColumn(name = "calendar_cell_id", referencedColumnName = "cell_id")
    public CalendarItemCell getCalendarItemCell() {
        return calendarItemCell;
    }

    public void setCalendarItemCell(CalendarItemCell calendarItemCell) {
        this.calendarItemCell = calendarItemCell;
    }

    @ManyToOne
    @JoinColumn(name = "class_type_id", referencedColumnName = "type_id")
    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }
}
