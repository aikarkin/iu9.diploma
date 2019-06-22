package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
public class Classroom {

    private int id;
    private String roomNumber;
    private Integer capacity;
    private Collection<ScheduleItemParity> scheduleItemParities;

    public Classroom() {
    }

    public Classroom(String roomNumber, Integer capacity) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.capacity = capacity;
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

    @Basic
    @Column(name = "room_number", nullable = false, length = 10)
    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    @Basic
    @Column(name = "capacity")
    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Classroom classroom = (Classroom) o;
        return id == classroom.id &&
                Objects.equals(roomNumber, classroom.roomNumber) &&
                Objects.equals(capacity, classroom.capacity);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, roomNumber, capacity);
    }

    @OneToMany(mappedBy = "classroom")
    public Collection<ScheduleItemParity> getScheduleItemParities() {
        return scheduleItemParities;
    }

    public void setScheduleItemParities(Collection<ScheduleItemParity> scheduleItem) {
        this.scheduleItemParities = scheduleItem;
    }

}
