package ru.bmstu.schedule.smtgen;

import java.util.HashMap;
import java.util.Map;

public class SubjectsPerWeek extends HashMap<LessonKind, Double> {
    private SubjectsPerWeek() {
        super();
    }

    public SubjectsPerWeek(double lecCount, double semCount, double labCount) {
        this.put(LessonKind.lec, lecCount);
        this.put(LessonKind.lab, labCount);
        this.put(LessonKind.sem, semCount);
    }
}