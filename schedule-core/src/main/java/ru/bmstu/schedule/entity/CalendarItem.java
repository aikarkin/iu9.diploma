package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "calendar_item")
public class CalendarItem {

    private int id;
    private Calendar calendar;
    private DepartmentSubject departmentSubject;
    private Set<CalendarItemCell> calendarItemCells = new HashSet<>();

    @Id
    @Column(name = "calendar_item_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "calendar_id", referencedColumnName = "id")
    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    @ManyToOne
    @JoinColumn(name = "department_subject_id", referencedColumnName = "id")
    public DepartmentSubject getDepartmentSubject() {
        return departmentSubject;
    }

    public void setDepartmentSubject(DepartmentSubject subject) {
        this.departmentSubject = subject;
    }

    @OneToMany(mappedBy = "calendarItem", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public Set<CalendarItemCell> getCalendarItemCells() {
        return calendarItemCells;
    }

    public void setCalendarItemCells(Set<CalendarItemCell> calendarItemCells) {
        this.calendarItemCells = calendarItemCells;
    }

    public void addItemCell(CalendarItemCell cell) {
        cell.setCalendarItem(this);
        this.getCalendarItemCells().add(cell);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalendarItem that = (CalendarItem) o;
        return id == that.id &&
                Objects.equals(calendar, that.calendar) &&
                Objects.equals(departmentSubject, that.departmentSubject) &&
                Objects.equals(calendarItemCells, that.calendarItemCells);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, calendar, departmentSubject);
    }

}
