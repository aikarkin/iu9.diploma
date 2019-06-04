package ru.bmstu.schedule.smtgen.model;

import ru.bmstu.schedule.smtgen.LessonKind;

import java.util.Objects;

public class TutorForLesson {

    private int tutorId;
    private int subjectId;
    private LessonKind kind;

    public TutorForLesson() {
    }

    public TutorForLesson(int tutorId, int subjectId, LessonKind kind) {
        this.tutorId = tutorId;
        this.subjectId = subjectId;
        this.kind = kind;
    }

    public int getTutorId() {
        return tutorId;
    }

    public void setTutorId(int tutorId) {
        this.tutorId = tutorId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public LessonKind getKind() {
        return kind;
    }

    public void setKind(LessonKind kind) {
        this.kind = kind;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TutorForLesson that = (TutorForLesson) o;
        return tutorId == that.tutorId &&
                subjectId == that.subjectId &&
                kind == that.kind;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tutorId, subjectId, kind);
    }

}
