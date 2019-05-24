package ru.bmstu.schedule.smtgen;

import com.microsoft.z3.Model;
import ru.bmstu.schedule.entity.Classroom;
import ru.bmstu.schedule.entity.Lecturer;
import ru.bmstu.schedule.entity.Subject;
import ru.bmstu.schedule.smtgen.model.SmtScheduleModelGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmtGenerator {
    private Map<Subject, Double> totalSubjectsPerWeak;
    private Map<Lecturer, Subject> subjectsOfLecturers;
    private List<Classroom> classrooms;

    private Map<Integer, Subject> idToSubj = new HashMap<>();
    private Map<Integer, Lecturer> idToLecturer = new HashMap<>();
    private Map<Classroom, Integer> classroomToId = new HashMap<>();
    private Map<Integer, Classroom> idToClassroom = new HashMap<>();

    private Map<Integer, Double> totalSubjectsPerWeakById = new HashMap<>();
    private Map<Integer, Integer> subjectsIdOfLecturerId = new HashMap<>();

    public SmtGenerator(
            Map<Subject, Double> totalSubjectsPerWeak,
            Map<Lecturer, Subject> subjectsOfLecturers,
            List<Classroom> classrooms
    ) {
        this.totalSubjectsPerWeak = totalSubjectsPerWeak;
        this.subjectsOfLecturers = subjectsOfLecturers;
        this.classrooms = classrooms;
    }


    public Schedule generateSchedule() {
        fillIdentifiersMaps();
        SmtScheduleModelGenerator scheduleModelGenerator = new SmtScheduleModelGenerator(
                totalSubjectsPerWeakById,
                subjectsIdOfLecturerId,
                new ArrayList<>(classroomToId.values())
        );
        Model scheduleModel = scheduleModelGenerator.createSmtModel();
        return transformModelToSchedule(scheduleModel);
    }

    private void fillIdentifiersMaps() {
        for (Subject subj : totalSubjectsPerWeak.keySet()) {
            idToSubj.put(subj.getId(), subj);
            totalSubjectsPerWeakById.put(subj.getId(), totalSubjectsPerWeak.get(subj));
        }

        for (Lecturer lecturer : subjectsOfLecturers.keySet()) {
            idToLecturer.put(lecturer.getId(), lecturer);
            subjectsIdOfLecturerId.put(subjectsOfLecturers.get(lecturer).getId(), lecturer.getId());
        }

        for (Classroom classroom : classrooms) {
            classroomToId.put(classroom, classroom.getId());
            idToClassroom.put(classroom.getId(), classroom);
        }
    }

    private Schedule transformModelToSchedule(Model scheduleModel) {
        return null;
    }

}
