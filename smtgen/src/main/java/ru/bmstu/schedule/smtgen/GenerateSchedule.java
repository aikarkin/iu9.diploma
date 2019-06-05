package ru.bmstu.schedule.smtgen;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.bmstu.schedule.dao.CalendarDao;
import ru.bmstu.schedule.dao.ClassroomDao;
import ru.bmstu.schedule.entity.Calendar;
import ru.bmstu.schedule.entity.*;

import java.util.*;

public class GenerateSchedule {

    private static final String[] rooms = new String[]{
            "330аю",
            "739л",
            "831л",
            "717л",
            "1015л",
            "1031л",
            "309ю",
            "501ю",
    };

    private static final Map<String, LessonKind> dbClassType2LessonKind;
    private static final String SPEC_CODE = "01.03.02_1";
    private static final String DEPT_CIPHER = "ИУ9";
    private static final int ENROLLMENT_YEAR = 2018;
    private static final int TERM_NO = 2;
    private static final int NO_OF_STUDY_WEEKS = 17;

    static {
        dbClassType2LessonKind = new HashMap<>();
        dbClassType2LessonKind.put("семинар", LessonKind.sem);
        dbClassType2LessonKind.put("лекция", LessonKind.lec);
        dbClassType2LessonKind.put("лабораторная работа", LessonKind.lab);
    }

    public static void main(String[] args) {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        CalendarDao calendarDao = new CalendarDao(sessionFactory);
        ClassroomDao classroomDao = new ClassroomDao(sessionFactory);

        Optional<Calendar> calendarOpt = calendarDao.findByStartYearAndDepartmentCodeAndSpecCode(ENROLLMENT_YEAR, DEPT_CIPHER, SPEC_CODE);

        if (!calendarOpt.isPresent()) {
            System.err.println("[error] Failed to find calendar with such parameters.");
            return;
        }

        Map<Subject, SubjectsPerWeek> subjectsPerWeekMap = new HashMap<>();
        List<LecturerSubject> lecturerSubjects = new ArrayList<>();
        List<Classroom> classrooms = new ArrayList<>();
        List<StudyGroup> groups = new ArrayList<>();

        for (CalendarItem item : calendarOpt.get().getCalendarItems()) {
            DepartmentSubject deptSubj = item.getDepartmentSubject();
            Subject subject = deptSubj.getSubject();

            for (CalendarItemCell itemCell : item.getCalendarItemCells()) {
                if (itemCell.getTerm().getNumber() == TERM_NO) {
                    SubjectsPerWeek subjPerWeek = new SubjectsPerWeek();

                    for (HoursPerClass hpc : itemCell.getHoursPerClasses()) {
                        int noOfHours = hpc.getNoOfHours();
                        ClassType classType = hpc.getClassType();
                        LessonKind kind = dbClassType2LessonKind.get(classType.getName());
                        if (kind != null && noOfHours > 0) {
                            subjPerWeek.put(kind, (double) noOfHours / (NO_OF_STUDY_WEEKS * 2.0));
                        }

                    }
                    subjectsPerWeekMap.put(subject, subjPerWeek);
                }
            }

            for (LecturerSubject lecSubj : deptSubj.getLecturerSubjects()) {
                if (subjectsPerWeekMap.containsKey(lecSubj.getDepartmentSubject().getSubject())) {
                    lecturerSubjects.add(lecSubj);
                }
            }
        }

//        for (StudyGroup group : calendarOpt.get().getStudyGroups()) {
//            if (group.getTerm().getNumber() == TERM_NO) {
//                groups.add(group);
//            }
//        }
        groups.add(calendarOpt.get().getStudyGroups().iterator().next());

        for (String roomNumber : rooms) {
            Optional<Classroom> roomOpt = classroomDao.findByRoomNumber(roomNumber);
            roomOpt.ifPresent(classrooms::add);
        }

        SmtScheduleGenerator scheduleGenerator = new SmtScheduleGenerator(
                subjectsPerWeekMap,
                lecturerSubjects,
                classrooms,
                groups
        );

        Schedule schedule = scheduleGenerator.generateSchedule();

    }

}
