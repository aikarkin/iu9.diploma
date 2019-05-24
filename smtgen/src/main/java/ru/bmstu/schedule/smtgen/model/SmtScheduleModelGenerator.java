package ru.bmstu.schedule.smtgen.model;

import com.microsoft.z3.Model;

import java.util.List;
import java.util.Map;

public class SmtScheduleModelGenerator {
    Map<Integer, Double> totalSubjectsPerWeak;
    Map<Integer, Integer> subjectsOfLecturers;
    List<Integer> rooms;

    public SmtScheduleModelGenerator(Map<Integer, Double> totalSubjectsPerWeak, Map<Integer, Integer> subjectsOfLecturers, List<Integer> rooms) {
        this.totalSubjectsPerWeak = totalSubjectsPerWeak;
        this.subjectsOfLecturers = subjectsOfLecturers;
        this.rooms = rooms;
    }

    public Model createSmtModel() {
        return null;
    }

}
