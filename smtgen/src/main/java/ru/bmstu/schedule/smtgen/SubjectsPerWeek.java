package ru.bmstu.schedule.smtgen;

import java.util.HashMap;

public class SubjectsPerWeek extends HashMap<LessonKind, Double> {

    public SubjectsPerWeek() {
        super();
    }

    public SubjectsPerWeek(double lecCount, double semCount, double labCount) {
        super();
        this.put(LessonKind.lec, lecCount);
        this.put(LessonKind.lab, labCount);
        this.put(LessonKind.sem, semCount);
    }
}