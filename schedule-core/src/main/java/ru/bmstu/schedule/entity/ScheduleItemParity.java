package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "schedule_item_parity")
public class ScheduleItemParity {
    private int id;
    private String dayParity;
    private ScheduleItem scheduleItem;
    private Classroom classroom;
    private ClassType classType;
    private Subject subject;
    private Set<Lecturer> lecturers = new HashSet<>();

    @Id
    @Column(name = "schedule_item_parity_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "day_parity", columnDefinition = "bpchar", length = 5)
    public String getDayParity() {
        return dayParity;
    }

    public void setDayParity(String dayParity) {
        this.dayParity = dayParity;
    }

    @ManyToOne
    @JoinColumn(name = "schedule_item_id", referencedColumnName = "schedule_item_id")
    public ScheduleItem getScheduleItem() {
        return scheduleItem;
    }

    public void setScheduleItem(ScheduleItem scheduleItem) {
        this.scheduleItem = scheduleItem;
    }

    @ManyToOne
    @JoinColumn(name = "classroom_id", referencedColumnName = "room_id")
    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    @ManyToOne
    @JoinColumn(name = "class_type_id", referencedColumnName = "type_id")
    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    @ManyToOne
    @JoinColumn(name = "subject_id", referencedColumnName = "subject_id")
    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleItemParity that = (ScheduleItemParity) o;
        return id == that.id &&
                Objects.equals(dayParity, that.dayParity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dayParity);
    }

    @ManyToMany(mappedBy="scheduleItemParities", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    public Set<Lecturer> getLecturers() {
        return lecturers;
    }

    void setLecturers(Set<Lecturer> lecturers) {
        this.lecturers = lecturers;
    }

    public void addLecturer(Lecturer lecturer) {
        lecturer.getScheduleItemParities().add(this);
        getLecturers().add(lecturer);
    }

    @Override
    public String toString() {
        return "ScheduleItemParity{" +
                "id=" + id +
                ", dayParity='" + dayParity + '\'' +
                ", scheduleItem=" + scheduleItem.getClassTime().toString() +
                ", classroom=" + classroom.getRoomNumber() +
                ", classType=" + classType.getName() +
                ", subject=" + subject.getName() +
                '}';
    }
}
