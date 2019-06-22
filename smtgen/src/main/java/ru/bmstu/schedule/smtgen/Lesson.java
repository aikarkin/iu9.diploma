package ru.bmstu.schedule.smtgen;

import ru.bmstu.schedule.entity.ClassType;
import ru.bmstu.schedule.entity.Classroom;
import ru.bmstu.schedule.entity.Tutor;
import ru.bmstu.schedule.entity.Subject;

import java.util.Objects;

public class Lesson {

    private Classroom classroom;
    private Tutor tutor;
    private Subject subject;
    private ClassType classType;

    public Lesson() {
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return Objects.equals(classroom, lesson.classroom) &&
                Objects.equals(tutor, lesson.tutor) &&
                Objects.equals(subject, lesson.subject) &&
                Objects.equals(classType, lesson.classType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classroom, tutor, subject, classType);
    }

    @Override
    public String toString() {
        return String.format(
                "(%s) %s %s %s",
                getClassType().getName().substring(0, 3),
                getSubject().getName(),
                classroom == null ? "" : classroom.getRoomNumber(),
                tutor == null ? "" : tutor.getInitials()
        );
    }

}
