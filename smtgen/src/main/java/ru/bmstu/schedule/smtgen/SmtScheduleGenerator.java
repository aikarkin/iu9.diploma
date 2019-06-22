package ru.bmstu.schedule.smtgen;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import ru.bmstu.schedule.entity.*;
import ru.bmstu.schedule.smtgen.model.ModelToScheduleTransformer;
import ru.bmstu.schedule.smtgen.model.SmtScheduleModelGenerator;
import ru.bmstu.schedule.smtgen.model.TutorForLesson;

import java.util.*;

public class SmtScheduleGenerator {

    private static final int UNKNOWN_TUTOR_ID = 0;
    private static final Map<String, LessonKind> CT_NAME_TO_KIND;

    static {
        CT_NAME_TO_KIND = new HashMap<>();
        CT_NAME_TO_KIND.put("семинар", LessonKind.sem);
        CT_NAME_TO_KIND.put("лекция", LessonKind.lec);
        CT_NAME_TO_KIND.put("лабораторная работа", LessonKind.lab);
    }

    private List<TutorForLesson> tutorForLessons = new ArrayList<>();
    private BiMap<Subject, Integer> subjectIdBiMap = HashBiMap.create();
    private BiMap<StudyGroup, Integer> groupIdBiMap = HashBiMap.create();
    private BiMap<Classroom, Integer> roomIdBiMap = HashBiMap.create();
    private Map<Integer, Tutor> lecturerIdToTutor = new HashMap<>();
    private Map<Integer, SubjectsPerWeek> subjectsPerWeakMap = new HashMap<>();
    private Map<LessonKind, ClassType> kindToClassType = new HashMap<>();

    public SmtScheduleGenerator(
            Map<Subject, SubjectsPerWeek> totalSubjectsPerWeak,
            Collection<TutorSubject> tutorSubjects,
            List<Classroom> classrooms,
            List<StudyGroup> groups,
            List<ClassType> classTypes
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

        for (ClassType classType : classTypes) {
            String ctName = classType.getName();
            if (CT_NAME_TO_KIND.containsKey(ctName)) {
                kindToClassType.put(CT_NAME_TO_KIND.get(ctName), classType);
            }
        }

        Set<Integer> subjectsSetForTutor = new HashSet<>();
        for (TutorSubject lecSubj : tutorSubjects) {
            TutorForLesson tutorForLesson = new TutorForLesson();
            String kindName = lecSubj.getClassType().getName();
            Subject subj = lecSubj.getDepartmentSubject().getSubject();
            Tutor lec = lecSubj.getTutor();
            int subjId = subj.getId();
            int lecId = lec.getId();

            if (!CT_NAME_TO_KIND.containsKey(kindName)) {
                continue;
            }
            LessonKind kind = CT_NAME_TO_KIND.get(kindName);

            tutorForLesson.setKind(kind);
            tutorForLesson.setSubjectId(subjId);
            tutorForLesson.setTutorId(lecId);
            subjectsSetForTutor.add(subjId);

            tutorForLessons.add(tutorForLesson);
            lecturerIdToTutor.put(lecId, lec);
        }

        for (int subjId : subjectsPerWeakMap.keySet()) {
            if (!subjectsSetForTutor.contains(subjId)) {
                for (LessonKind kind : LessonKind.values()) {
                    tutorForLessons.add(new TutorForLesson(UNKNOWN_TUTOR_ID, subjId, kind));
                }
            }
        }
    }


    public Map<StudyGroup, Schedule> generateSchedule() throws RuntimeException {
        SmtScheduleModelGenerator modelGenerator = new SmtScheduleModelGenerator(
                subjectsPerWeakMap,
                tutorForLessons,
                new ArrayList<>(roomIdBiMap.values()),
                new ArrayList<>(groupIdBiMap.values())
        );

        if (!modelGenerator.satisfies()) {
            throw new RuntimeException("Unable to build model with provided parameters");
        }

        ModelToScheduleTransformer transformer = new ModelToScheduleTransformer(
                modelGenerator,
                subjectIdBiMap.inverse(),
                lecturerIdToTutor,
                groupIdBiMap.inverse(),
                roomIdBiMap.inverse(),
                kindToClassType
        );
        return transformer.transform();
    }

}
