package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.sql.Time;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "class_time", schema = "public", catalog = "schedule")
public class ClassTime {
    private int id;
    private int noOfClass;
    private Time startsAt;
    private Time endsAt;
    private Collection<ScheduleItem> scheduleItems;

    @Id
    @Column(name = "class_time_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "no_of_class", nullable = false)
    public int getNoOfClass() {
        return noOfClass;
    }

    public void setNoOfClass(int noOfClass) {
        this.noOfClass = noOfClass;
    }

    @Basic
    @Column(name = "starts_at", nullable = false)
    public Time getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(Time startsAt) {
        this.startsAt = startsAt;
    }

    @Basic
    @Column(name = "ends_at", nullable = false)
    public Time getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(Time endsAt) {
        this.endsAt = endsAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassTime classTime = (ClassTime) o;
        return id == classTime.id &&
                noOfClass == classTime.noOfClass &&
                Objects.equals(startsAt, classTime.startsAt) &&
                Objects.equals(endsAt, classTime.endsAt);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, noOfClass, startsAt, endsAt);
    }

    @OneToMany(mappedBy = "classTime")
    public Collection<ScheduleItem> getScheduleItems() {
        return scheduleItems;
    }

    public void setScheduleItems(Collection<ScheduleItem> scheduleItems) {
        this.scheduleItems = scheduleItems;
    }
}
