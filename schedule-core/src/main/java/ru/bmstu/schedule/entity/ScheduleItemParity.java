package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "schedule_item_parity")
public class ScheduleItemParity {

    private int id;
    private String dayParity;
    private ScheduleItem scheduleItem;
    private TutorSubject tutorSubject;
    private Classroom classroom;
    private ClassType classType;

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
    @Column(name = "day_parity", columnDefinition = "bpchar", length = 5)
    public String getDayParity() {
        return dayParity;
    }

    public void setDayParity(String dayParity) {
        this.dayParity = dayParity;
    }

    @ManyToOne
    @JoinColumn(name = "schedule_item_id", referencedColumnName = "id")
    public ScheduleItem getScheduleItem() {
        return scheduleItem;
    }

    public void setScheduleItem(ScheduleItem scheduleItem) {
        this.scheduleItem = scheduleItem;
    }

    @ManyToOne
    @JoinColumn(name = "classroom_id", referencedColumnName = "id")
    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    @ManyToOne
    @JoinColumn(name = "class_type_id", referencedColumnName = "id")
    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    @ManyToOne
    @JoinColumn(name = "tutor_subject_id", referencedColumnName = "id")
    public TutorSubject getTutorSubject() {
        return tutorSubject;
    }

    public void setTutorSubject(TutorSubject tutorSubject) {
        this.tutorSubject = tutorSubject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleItemParity that = (ScheduleItemParity) o;
        return id == that.id &&
                Objects.equals(dayParity, that.dayParity) &&
                Objects.equals(scheduleItem, that.scheduleItem) &&
                Objects.equals(tutorSubject, that.tutorSubject) &&
                Objects.equals(classroom, that.classroom) &&
                Objects.equals(classType, that.classType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dayParity, scheduleItem, tutorSubject, classroom, classType);
    }

    @Override
    public String toString() {
        Tutor lect = tutorSubject == null ? null : tutorSubject.getTutor();
        DepartmentSubject deptSubj = tutorSubject == null ? null : tutorSubject.getDepartmentSubject();
        Subject subj = deptSubj == null ? null : deptSubj.getSubject();

        return "ScheduleItemParity{" +
                "id=" + id +
                ", dayParity='" + dayParity + '\'' +
                ", lecturer=" + (lect == null ? "" : lect.getInitials()) +
                ", subject=" + (subj == null ? "" : subj.getName()) +
                ", classroom=" + (classroom == null ? "" : classroom.getRoomNumber()) +
                ", classType=" + (classType == null ? "" : classType.getName().substring(0, 3)) +
                '}';
    }

}
