package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "calendar_item_cell", schema = "public", catalog = "schedule")
public class CalendarItemCell {
    private int id;
    private CalendarItem calendarItem;
    private Term term;
    private Collection<HoursPerClass> hoursPerClasses;

    @Id
    @Column(name = "cell_id", nullable = false)
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
        CalendarItemCell that = (CalendarItemCell) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @ManyToOne
    @JoinColumn(name = "calendar_item_id", referencedColumnName = "calendar_item_id")
    public CalendarItem getCalendarItem() {
        return calendarItem;
    }

    public void setCalendarItem(CalendarItem calendarItem) {
        this.calendarItem = calendarItem;
    }

    @ManyToOne
    @JoinColumn(name = "term_id", referencedColumnName = "term_id")
    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    @OneToMany(mappedBy = "calendarItemCell")
    public Collection<HoursPerClass> getHoursPerClasses() {
        return hoursPerClasses;
    }

    public void setHoursPerClasses(Collection<HoursPerClass> hoursPerClasses) {
        this.hoursPerClasses = hoursPerClasses;
    }
}
