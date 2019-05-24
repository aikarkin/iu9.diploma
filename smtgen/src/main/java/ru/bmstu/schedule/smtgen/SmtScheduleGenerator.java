package ru.bmstu.schedule.smtgen;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.microsoft.z3.Model;
import ru.bmstu.schedule.entity.Classroom;
import ru.bmstu.schedule.entity.Lecturer;
import ru.bmstu.schedule.entity.StudyGroup;
import ru.bmstu.schedule.entity.Subject;
import ru.bmstu.schedule.smtgen.model.SmtScheduleModelGenerator;

import java.util.*;

public class SmtScheduleGenerator {

    private BiMap<Subject, Integer> subjectIdBiMap = HashBiMap.create();
    private BiMap<StudyGroup, Integer> groupIdBiMap = HashBiMap.create();
    private BiMap<Classroom, Integer> roomIdBiMap = HashBiMap.create();
    private BiMap<Lecturer, Integer> tutorIdBiMap = HashBiMap.create();
    private Map<Integer, Integer> tutor2SubjMap = new HashMap<>();
    private Map<Integer, SubjectsPerWeek> subjectsPerWeakMap = new HashMap<>();

    public SmtScheduleGenerator(
            Map<Subject, SubjectsPerWeek> totalSubjectsPerWeak,
            Map<Lecturer, Subject> subjectsOfLecturers,
            List<Classroom> classrooms,
            List<StudyGroup> groups
    ) {
        for(Subject subj : totalSubjectsPerWeak.keySet()) {
            subjectIdBiMap.put(subj, subj.getId());
            subjectsPerWeakMap.put(subj.getId(), totalSubjectsPerWeak.get(subj));
        }

        for(StudyGroup group : groups) {
            groupIdBiMap.put(group, group.getId());
        }

        for(Classroom room : classrooms) {
            roomIdBiMap.put(room, room.getId());
        }

        for(Lecturer lecturer : subjectsOfLecturers.keySet()) {
            tutorIdBiMap.put(lecturer, lecturer.getId());
            tutor2SubjMap.put(lecturer.getId(), subjectsOfLecturers.get(lecturer).getId());
        }
    }


    public Schedule generateSchedule() throws RuntimeException {
        SmtScheduleModelGenerator modelGenerator = new SmtScheduleModelGenerator(
                subjectsPerWeakMap,
                tutor2SubjMap,
                new ArrayList<>(roomIdBiMap.values()),
                new ArrayList<>(groupIdBiMap.values())
        );

        Optional<Model> smtModel = modelGenerator.createSmtModel();
        if(!smtModel.isPresent()) {
            throw new RuntimeException("No schedule exists provided parameters. Please try to change input params.");
        }

        return transformModelToSchedule(smtModel.get());
    }

    private Schedule transformModelToSchedule(Model model) {
        return null;
    }

}
