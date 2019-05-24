package ru.bmstu.schedule.smtgen;

import ru.bmstu.schedule.entity.Classroom;
import ru.bmstu.schedule.entity.Lecturer;
import ru.bmstu.schedule.entity.Subject;

import java.util.Objects;

public class Lesson {

    private Classroom classroom;
    private Lecturer lecturer;
    private Subject subject;

    public Lesson() {
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public Lecturer getLecturer() {
        return lecturer;
    }

    public void setLecturer(Lecturer lecturer) {
        this.lecturer = lecturer;
    }

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
        Lesson lesson = (Lesson) o;
        return Objects.equals(classroom, lesson.classroom) &&
                Objects.equals(lecturer, lesson.lecturer) &&
                Objects.equals(subject, lesson.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classroom, lecturer, subject);
    }

}
