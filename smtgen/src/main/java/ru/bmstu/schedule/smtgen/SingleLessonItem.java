package ru.bmstu.schedule.smtgen;

import java.util.Objects;

public class SingleLessonItem implements LessonItem {
    private int index;
    private Lesson lesson;

    public SingleLessonItem() {
    }

    public SingleLessonItem(int index, Lesson lesson) {
        this.index = index;
        this.lesson = lesson;
    }

    @Override
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingleLessonItem that = (SingleLessonItem) o;
        return index == that.index &&
                Objects.equals(lesson, that.lesson);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, lesson);
    }

}
