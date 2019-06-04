package ru.bmstu.schedule.smtgen;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.microsoft.z3.Model;
import ru.bmstu.schedule.entity.Classroom;
import ru.bmstu.schedule.entity.LecturerSubject;
import ru.bmstu.schedule.entity.StudyGroup;
import ru.bmstu.schedule.entity.Subject;
import ru.bmstu.schedule.smtgen.model.SmtScheduleModelGenerator;
import ru.bmstu.schedule.smtgen.model.TutorForLesson;

import java.util.*;

public class SmtScheduleGenerator {

    private BiMap<Subject, Integer> subjectIdBiMap = HashBiMap.create();
    private BiMap<StudyGroup, Integer> groupIdBiMap = HashBiMap.create();
    private BiMap<Classroom, Integer> roomIdBiMap = HashBiMap.create();
    private List<TutorForLesson> tutorForLessons = new ArrayList<>();
    private Map<Integer, SubjectsPerWeek> subjectsPerWeakMap = new HashMap<>();

    public SmtScheduleGenerator(
            Map<Subject, SubjectsPerWeek> totalSubjectsPerWeak,
            List<LecturerSubject> lecturerSubjects,
            List<Classroom> classrooms,
            List<StudyGroup> groups
    ) {
        for (Subject subj : totalSubjectsPerWeak.keySet()) {
            subjectIdBiMap.put(subj, subj.getId());
            subjectsPerWeakMap.put(subj.getId(), totalSubjectsPerWeak.get(subj));
        }

        for (StudyGroup group : groups) {
            groupIdBiMap.put(group, group.getId());
        }

        for (Classroom room : classrooms) {
            roomIdBiMap.put(room, room.getId());
        }

        Set<Integer> subjectsSetForTutor = new HashSet<>();
        for (LecturerSubject lecSubj : lecturerSubjects) {
            TutorForLesson tutorForLesson = new TutorForLesson();
            String kindName = lecSubj.getClassType().getName();
            LessonKind kind;
            if (kindName.equals("лекция")) {
                kind = LessonKind.lec;
            } else if (kindName.equals("семинар")) {
                kind = LessonKind.sem;
            } else if (kindName.equals("лабораторная работа")) {
                kind = LessonKind.lab;
            } else {
                continue;
            }

            int subjId = lecSubj.getDepartmentSubject().getSubject().getId();
            tutorForLesson.setKind(kind);
            tutorForLesson.setSubjectId(subjId);
            tutorForLesson.setTutorId(lecSubj.getLecturer().getId());
            subjectsSetForTutor.add(subjId);

            tutorForLessons.add(tutorForLesson);
        }

        for (int subjId : subjectsPerWeakMap.keySet()) {
            if (!subjectsSetForTutor.contains(subjId)) {
                for (LessonKind kind : LessonKind.values()) {
                    tutorForLessons.add(new TutorForLesson(0, subjId, kind));
                }
            }
        }
    }


    public Schedule generateSchedule() throws RuntimeException {
        SmtScheduleModelGenerator modelGenerator = new SmtScheduleModelGenerator(
                subjectsPerWeakMap,
                tutorForLessons,
                new ArrayList<>(roomIdBiMap.values()),
                new ArrayList<>(groupIdBiMap.values())
        );

        Optional<Model> smtModel = modelGenerator.createSmtModel();
        if (!smtModel.isPresent()) {
            throw new RuntimeException("No schedule exists provided parameters. Please try to change input params.");
        }

        return transformModelToSchedule(smtModel.get());
    }

    private Schedule transformModelToSchedule(Model model) {
        return null;
    }

}
