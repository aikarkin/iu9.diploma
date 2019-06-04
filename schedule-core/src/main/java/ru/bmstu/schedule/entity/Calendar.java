package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "calendar")
public class Calendar {

    private int id;
    private int startYear;
    private DepartmentSpecialization departmentSpecialization;
    private Set<CalendarItem> calendarItems = new HashSet<>();
    private Set<StudyGroup> studyGroups = new HashSet<>();

    public Calendar() {
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
    @JoinColumn(name = "dept_to_spec_id", referencedColumnName = "id")
    public DepartmentSpecialization getDepartmentSpecialization() {
        return departmentSpecialization;
    }

    public void setDepartmentSpecialization(DepartmentSpecialization departmentSpecialization) {
        this.departmentSpecialization = departmentSpecialization;
    }

    @OneToMany(mappedBy = "calendar", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public Set<CalendarItem> getCalendarItems() {
        return calendarItems;
    }

    public void setCalendarItems(Set<CalendarItem> calendarItems) {
        this.calendarItems = calendarItems;
    }

    @OneToMany(mappedBy = "calendar", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public Set<StudyGroup> getStudyGroups() {
        return studyGroups;
    }

    public void setStudyGroups(Set<StudyGroup> studyGroups) {
        this.studyGroups = studyGroups;
    }

    public void addCalendarItem(CalendarItem item) {
        item.setCalendar(this);
        getCalendarItems().add(item);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Calendar calendar = (Calendar) o;
        return id == calendar.id &&
                startYear == calendar.startYear &&
                Objects.equals(departmentSpecialization, calendar.departmentSpecialization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startYear, departmentSpecialization);
    }

}
