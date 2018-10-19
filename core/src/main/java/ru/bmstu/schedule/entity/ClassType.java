package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "class_type", schema = "public", catalog = "schedule")
public class ClassType {
    private int id;
    private String name;
    private Collection<HoursPerClass> hoursPerClasses;
    private Collection<ScheduleItemParity> scheduleItemParities;

    @Id
    @Column(name = "type_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "type_name", nullable = false, length = -1)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassType classType = (ClassType) o;
        return id == classType.id &&
                Objects.equals(name, classType.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name);
    }

    @OneToMany(mappedBy = "classType")
    public Collection<HoursPerClass> getHoursPerClasses() {
        return hoursPerClasses;
    }

    public void setHoursPerClasses(Collection<HoursPerClass> hoursPerClasses) {
        this.hoursPerClasses = hoursPerClasses;
    }

    @OneToMany(mappedBy = "classType")
    public Collection<ScheduleItemParity> getScheduleItemParities() {
        return scheduleItemParities;
    }

    public void setScheduleItemParities(Collection<ScheduleItemParity> scheduleItemParities) {
        this.scheduleItemParities = scheduleItemParities;
    }
}
