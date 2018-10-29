package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "schedule_item", schema = "public", catalog = "schedule")
public class ScheduleItem {
    private int id;
    private ScheduleDay scheduleDay;
    private ClassTime classTime;
    private Collection<ScheduleItemParity> scheduleItemParities;

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
        return id == that.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
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

    @OneToMany(mappedBy = "scheduleItem", cascade = CascadeType.ALL)
    public Collection<ScheduleItemParity> getScheduleItemParities() {
        return scheduleItemParities;
    }

    public void setScheduleItemParities(Collection<ScheduleItemParity> scheduleItemParities) {
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
                ", scheduleDay=" + scheduleDay +
                ", classTime=" + classTime +
                '}';
    }
}
