package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "schedule_item")
public class ScheduleItem {
    private int id;
    private ScheduleDay scheduleDay;
    private ClassTime classTime;
    private Set<ScheduleItemParity> scheduleItemParities = new HashSet<>();

    @Id
    @Column(name = "schedule_item_id", nullable = false)
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
        ScheduleItem that = (ScheduleItem) o;
        return id == that.id &&
                Objects.equals(scheduleDay, that.scheduleDay) &&
                Objects.equals(classTime, that.classTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, scheduleDay, classTime);
    }

    @ManyToOne
    @JoinColumn(name = "day_id", referencedColumnName = "day_id")
    public ScheduleDay getScheduleDay() {
        return scheduleDay;
    }

    public void setScheduleDay(ScheduleDay scheduleDay) {
        this.scheduleDay = scheduleDay;
    }

    @ManyToOne
    @JoinColumn(name = "class_time_id", referencedColumnName = "class_time_id")
    public ClassTime getClassTime() {
        return classTime;
    }

    public void setClassTime(ClassTime classTime) {
        this.classTime = classTime;
    }

    @OneToMany(mappedBy = "scheduleItem", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Set<ScheduleItemParity> getScheduleItemParities() {
        return scheduleItemParities;
    }

    public void setScheduleItemParities(Set<ScheduleItemParity> scheduleItemParities) {
        this.scheduleItemParities = scheduleItemParities;
    }

    public void addItemParity(ScheduleItemParity itemParity) {
        itemParity.setScheduleItem(this);
        getScheduleItemParities().add(itemParity);
    }

    @Override
    public String toString() {
        return "ScheduleItem{" +
                "id=" + id +
                ", scheduleDay=" + scheduleDay.getDayOfWeek().getShortName() +
                ", classTime=" + classTime.toString() +
                '}';
    }
}
