package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "schedule_item_parity")
public class ScheduleItemParity {

    private int id;
    private String dayParity;
    private ScheduleItem scheduleItem;
    private LecturerSubject lecturerSubject;
    private Classroom classroom;
    private ClassType classType;

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
    @JoinColumn(name = "lec_subj_id", referencedColumnName = "id")
    public LecturerSubject getLecturerSubject() {
        return lecturerSubject;
    }

    public void setLecturerSubject(LecturerSubject lecturerSubject) {
        this.lecturerSubject = lecturerSubject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleItemParity that = (ScheduleItemParity) o;
        return id == that.id &&
                Objects.equals(dayParity, that.dayParity) &&
                Objects.equals(scheduleItem, that.scheduleItem) &&
                Objects.equals(lecturerSubject, that.lecturerSubject) &&
                Objects.equals(classroom, that.classroom) &&
                Objects.equals(classType, that.classType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dayParity, scheduleItem, lecturerSubject, classroom, classType);
    }

}
