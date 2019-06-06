package ru.bmstu.schedule.smtgen;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.bmstu.schedule.dao.*;
import ru.bmstu.schedule.entity.Calendar;
import ru.bmstu.schedule.entity.DayOfWeek;
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

    private static final String SPEC_CODE = "01.03.02_1";
    private static final String DEPT_CIPHER = "ИУ9";
    private static final int ENROLLMENT_YEAR = 2018;
    private static final int TERM_NO = 2;

    private static final int NO_OF_STUDY_WEEKS = 17;
    private static final int MAX_GROUPS_COUNT = 2;
    private static final String PARITY_ALWAYS = "ЧС/ЗН";
    private static final String PARITY_NUM = "ЧС";
    private static final String PARITY_DEN = "ЗН";

    private static final Map<String, LessonKind> CLASS_TYPE_TO_LESSON_KIND;
    private static SessionFactory sessionFactory;

    static {
        CLASS_TYPE_TO_LESSON_KIND = new HashMap<>();
        CLASS_TYPE_TO_LESSON_KIND.put("семинар", LessonKind.sem);
        CLASS_TYPE_TO_LESSON_KIND.put("лекция", LessonKind.lec);
        CLASS_TYPE_TO_LESSON_KIND.put("лабораторная работа", LessonKind.lab);
    }

    private Map<Subject, DepartmentSubject> departmentSubjectMap = new HashMap<>();

    private ScheduleDayDao scheduleDayDao;
    private LecturerSubjectDao lecSubjDao;
    private LecturerDao lecDao;
    private CalendarDao calendarDao;
    private ClassroomDao classroomDao;
    private ClassTimeDao classTimeDao;
    private ClassTypeDao classTypeDao;
    private WeekDao weekDao;

    public static void main(String[] args) {
        GenerateSchedule genSchedule = new GenerateSchedule();
        try {
            genSchedule.initDaoObjects();
            genSchedule.runScheduleGeneration();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sessionFactory.close();
        }
    }

    private void initDaoObjects() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        scheduleDayDao = new ScheduleDayDao(sessionFactory);
        lecSubjDao = new LecturerSubjectDao(sessionFactory);
        classroomDao = new ClassroomDao(sessionFactory);
        classTypeDao = new ClassTypeDao(sessionFactory);
        classTimeDao = new ClassTimeDao(sessionFactory);
        calendarDao = new CalendarDao(sessionFactory);
        lecDao = new LecturerDao(sessionFactory);
        weekDao = new WeekDao(sessionFactory);
    }

    private void runScheduleGeneration() throws RuntimeException {
        Map<StudyGroup, Schedule> schedules = generateSchedules();
        printSchedules(schedules);
        removeSchedules(schedules);
        persistSchedules(schedules);
    }

    private void removeSchedules(Map<StudyGroup, Schedule> schedules) {
        for (StudyGroup studyGroup : schedules.keySet()) {
            for (ScheduleDay scheduleDay : studyGroup.getScheduleDays()) {
                scheduleDayDao.delete(scheduleDay);
            }
        }
    }

    private Map<StudyGroup, Schedule> generateSchedules() throws RuntimeException {
        Optional<Calendar> calendarOpt = calendarDao.findByStartYearAndDepartmentCodeAndSpecCode(ENROLLMENT_YEAR, DEPT_CIPHER, SPEC_CODE);

        if (!calendarOpt.isPresent()) {
            throw new RuntimeException("Failed to find calendar with such parameters.");
        }

        Map<Subject, SubjectsPerWeek> subjectsPerWeekMap = new HashMap<>();
        List<Classroom> classrooms = new ArrayList<>();
        List<StudyGroup> groups = new ArrayList<>();
        List<LecturerSubject> lecturerSubjects = new ArrayList<>();

        for (CalendarItem item : calendarOpt.get().getCalendarItems()) {
            DepartmentSubject deptSubj = item.getDepartmentSubject();
            Subject subject = deptSubj.getSubject();

            departmentSubjectMap.put(subject, deptSubj);
            for (CalendarItemCell itemCell : item.getCalendarItemCells()) {
                if (itemCell.getTerm().getNumber() == TERM_NO) {
                    SubjectsPerWeek subjPerWeek = new SubjectsPerWeek();

                    for (HoursPerClass hpc : itemCell.getHoursPerClasses()) {
                        int noOfHours = hpc.getNoOfHours();
                        ClassType classType = hpc.getClassType();
                        LessonKind kind = CLASS_TYPE_TO_LESSON_KIND.get(classType.getName());
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
        int groupIndex = 0;
        for (StudyGroup group : calendarOpt.get().getStudyGroups()) {
            if (group.getTerm().getNumber() == TERM_NO && groupIndex < MAX_GROUPS_COUNT) {
                groups.add(group);
                groupIndex++;
            }
        }

        for (String roomNumber : rooms) {
            Optional<Classroom> roomOpt = classroomDao.findByRoomNumber(roomNumber);
            roomOpt.ifPresent(classrooms::add);
        }

        List<ClassType> classTypes = new ArrayList<>();
        for (String typeName : CLASS_TYPE_TO_LESSON_KIND.keySet()) {
            Optional<ClassType> ctOpt = classTypeDao.findByName(typeName);
            if (!ctOpt.isPresent()) {
                throw new IllegalStateException("Unknown class type: " + typeName);
            }

            classTypes.add(ctOpt.get());
        }

        SmtScheduleGenerator scheduleGenerator = new SmtScheduleGenerator(
                subjectsPerWeekMap,
                lecturerSubjects,
                classrooms,
                groups,
                classTypes
        );

        return scheduleGenerator.generateSchedule();
    }

    private void persistSchedules(Map<StudyGroup, Schedule> scheduleMap) {
        for (StudyGroup studyGroup : scheduleMap.keySet()) {
            Schedule schedule = scheduleMap.get(studyGroup);
            for (DayEntry dayEntry : schedule.getDayEntries()) {
                ScheduleDay scheduleDay;
                try {
                    scheduleDay = convertToScheduleDay(dayEntry);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    System.out.println("[error] " + e.getMessage());
                    continue;
                }

                scheduleDay.setStudyGroup(studyGroup);
                scheduleDayDao.create(scheduleDay);
            }
        }
    }

    private ScheduleDay convertToScheduleDay(DayEntry entry) throws IllegalStateException {
        String weekAlias = entry.getDayOfWeek().getAlias();
        Optional<DayOfWeek> weekOpt = weekDao.findByShortName(weekAlias);

        if (!weekOpt.isPresent()) {
            throw new IllegalStateException("Invalid day of week alias: " + weekAlias);
        }

        ScheduleDay dayEntity = new ScheduleDay();
        dayEntity.setDayOfWeek(weekOpt.get());
        for (int i = 0; i < entry.getItems().length; i++) {
            ScheduleItem scheduleItem;
            LessonItem lessonItem = entry.getItems()[i];
            if (lessonItem == null) {
                continue;
            }

            scheduleItem = convertToScheduleItem(lessonItem);
            dayEntity.addScheduleItem(scheduleItem);
        }

        return dayEntity;
    }

    private ScheduleItem convertToScheduleItem(LessonItem lessonItem) throws RuntimeException {
        ScheduleItem scheduleItem = new ScheduleItem();
        Optional<ClassTime> ctOpt = classTimeDao.findByOrderNumber(lessonItem.getIndex() + 1);
        if (!ctOpt.isPresent()) {
            String msg = String.format("[error] Invalid class time number: %d%n", lessonItem.getIndex() + 1);
            throw new RuntimeException(msg);
        }

        scheduleItem.setClassTime(ctOpt.get());

        if (lessonItem instanceof SingleLessonItem) {
            Lesson lesson = ((SingleLessonItem) lessonItem).getLesson();
            if (lesson != null) {
                scheduleItem.addItemParity(convertToItemParity(lesson, PARITY_ALWAYS));
            }
        } else if (lessonItem instanceof PairLessonItem) {
            PairLessonItem pairLessonItem = (PairLessonItem) lessonItem;
            Lesson numerator = pairLessonItem.getNumerator();
            Lesson denominator = pairLessonItem.getDenominator();

            if (numerator != null) {
                scheduleItem.addItemParity(convertToItemParity(numerator, PARITY_NUM));
            }
            if (denominator != null) {
                scheduleItem.addItemParity(convertToItemParity(denominator, PARITY_DEN));
            }
        }

        return scheduleItem;
    }

    private ScheduleItemParity convertToItemParity(Lesson lesson, String parity) throws RuntimeException {
        ScheduleItemParity itemParity = new ScheduleItemParity();
        ClassType classType = lesson.getClassType();
        itemParity.setClassroom(lesson.getClassroom());
        itemParity.setClassType(classType);
        itemParity.setDayParity(parity);
        Lecturer lecturer = lesson.getLecturer();
        Subject subject = lesson.getSubject();

        DepartmentSubject deptSubj = departmentSubjectMap.get(subject);
        LecturerSubject lecSubj = null;

        if (lecturer == null) {
            Lecturer unknownLec = lecDao.fetchUnknownLecturer();
            Optional<LecturerSubject> lecSubjOpt = lecSubjDao.findByLecturerAndDepartmentSubjectAndClassType(
                    unknownLec,
                    deptSubj,
                    classType
            );

            if (!lecSubjOpt.isPresent()) {
                lecSubj = new LecturerSubject();
                lecSubj.setLecturer(unknownLec);
                lecSubj.setDepartmentSubject(deptSubj);
                lecSubj.setClassType(classType);
                Integer lecSubjId = lecSubjDao.create(lecSubj);
                lecSubj.setId(lecSubjId);
            } else {
                lecSubj = lecSubjOpt.get();
            }
        } else {
            Optional<LecturerSubject> lecSubjOpt = lecSubjDao.findByLecturerAndDepartmentSubjectAndClassType(
                    lecturer,
                    deptSubj,
                    classType
            );
            if (!lecSubjOpt.isPresent()) {
                String msg = String.format(
                        "Invalid SMT Model - illegal lecturer '%s' for subject '%s'",
                        lecturer.getInitials(),
                        subject.getName()
                );
                throw new RuntimeException(msg);
            }
            lecSubj = lecSubjOpt.get();
        }


        itemParity.setLecturerSubject(lecSubj);

        return itemParity;
    }

    private static void printSchedules(Map<StudyGroup, Schedule> scheduleMap) {
        for (Map.Entry<StudyGroup, Schedule> scheduleEntry : scheduleMap.entrySet()) {
            System.out.printf("Schedule for group: %s%n", groupRepr(scheduleEntry.getKey()));
            System.out.println(scheduleEntry.getValue());
            System.out.println("-----------------------------");
        }
    }

    private static String groupRepr(StudyGroup studyGroup) {
        DepartmentSpecialization deptSpec = studyGroup.getCalendar().getDepartmentSpecialization();
        Specialization spec = deptSpec.getSpecialization();
        Department dept = deptSpec.getDepartment();
        int groupNo = studyGroup.getNumber();
        int termNo = studyGroup.getTerm().getNumber();
        int specNo = spec.getNumberInSpeciality();
        String specCode = spec.getSpeciality().getCode();
        String deptCipher = dept.getCipher();

        return String.format("%s-%d%d (%s_%d)", deptCipher, termNo, groupNo, specCode, specNo);
    }

}