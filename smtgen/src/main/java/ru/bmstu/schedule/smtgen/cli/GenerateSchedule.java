package ru.bmstu.schedule.smtgen.cli;

import org.apache.commons.cli.ParseException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.bmstu.schedule.dao.*;
import ru.bmstu.schedule.entity.Calendar;
import ru.bmstu.schedule.entity.DayOfWeek;
import ru.bmstu.schedule.entity.*;
import ru.bmstu.schedule.smtgen.*;

import java.util.*;

public class GenerateSchedule {

    private static final int NO_OF_CLASS_ROOMS = 10;
    private static final int NO_OF_STUDY_WEEKS = 17;
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
    private StudyGroupDao studyGroupDao;
    private LecturerSubjectDao lecSubjDao;
    private LecturerDao lecDao;
    private CalendarDao calendarDao;
    private ClassroomDao classroomDao;
    private ClassTimeDao classTimeDao;
    private ClassTypeDao classTypeDao;
    private WeekDao weekDao;

    public static void main(String[] args) {
        GenerateSchedule genSchedule = new GenerateSchedule();
        CommandLineParser parser = new CommandLineParser();
        try {
            genSchedule.initDaoObjects();
            genSchedule.runScheduleGeneration(parser.parse(args));
        } catch (ParseException e) {
            if (e.getMessage() != null) {
                System.err.println("Невалидные параметры командной строки: " + e.getMessage());
            }
            parser.printHelp();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sessionFactory.close();
        }
    }

    private void initDaoObjects() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        studyGroupDao = new StudyGroupDao(sessionFactory);
        scheduleDayDao = new ScheduleDayDao(sessionFactory);
        lecSubjDao = new LecturerSubjectDao(sessionFactory);
        classroomDao = new ClassroomDao(sessionFactory);
        classTypeDao = new ClassTypeDao(sessionFactory);
        classTimeDao = new ClassTimeDao(sessionFactory);
        calendarDao = new CalendarDao(sessionFactory);
        lecDao = new LecturerDao(sessionFactory);
        weekDao = new WeekDao(sessionFactory);
    }

    private void runScheduleGeneration(ScheduleConfiguration config) throws RuntimeException {
        Map<StudyGroup, Schedule> schedules = generateSchedules(config);
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

    private void checkAllGroups(List<StudyGroup> groups) {
        if (groups.size() == 0)
            return;

        Calendar firstCalendar = groups.get(0).getCalendar();
        for (int i = 1; i < groups.size(); i++) {
            if (!groups.get(i).getCalendar().equals(firstCalendar)) {
                throw new RuntimeException("Невозможно сгенерировать рассписание для данных групп: группы имеют разные учебные планы");
            }
        }
    }

    private Map<StudyGroup, Schedule> generateSchedules(ScheduleConfiguration config) throws RuntimeException {
        List<StudyGroup> groups = new ArrayList<>();
        Calendar calendar;
        int term;
        if (config.getGroupCiphers() != null) {
            for (String grCipher : config.getGroupCiphers()) {
                Optional<StudyGroup> grOpt = studyGroupDao.findByCipher(grCipher);
                if (!grOpt.isPresent()) {
                    throw new RuntimeException("Группа с таким шифром не найдена: " + grCipher);
                }
                groups.add(grOpt.get());
            }
            checkAllGroups(groups);
            calendar = groups.get(0).getCalendar();
            term = groups.get(0).getTerm().getNumber();
        } else {
            int year = config.getEnrollmentYear();
            term = config.getNoOfTerm();
            String deptCipher = config.getDepartmentCipher();
            String specCode = config.getSpecializationCode();

            Optional<Calendar> calendarOpt = calendarDao.findByStartYearAndDepartmentCodeAndSpecCode(year, deptCipher, specCode);

            if (!calendarOpt.isPresent()) {
                throw new RuntimeException("Учебный план с заданными параметрами не найден");
            }
            calendar = calendarOpt.get();

            for (StudyGroup group : calendar.getStudyGroups()) {
                if (group.getTerm().getNumber() == term) {
                    groups.add(group);
                }
            }
        }

        Map<Subject, SubjectsPerWeek> subjectsPerWeekMap = new HashMap<>();
        List<LecturerSubject> lecturerSubjects = new ArrayList<>();

        for (CalendarItem item : calendar.getCalendarItems()) {
            DepartmentSubject deptSubj = item.getDepartmentSubject();
            Subject subject = deptSubj.getSubject();

            departmentSubjectMap.put(subject, deptSubj);
            for (CalendarItemCell itemCell : item.getCalendarItemCells()) {
                if (itemCell.getTerm().getNumber() == term) {
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

        List<Classroom> classrooms = classroomDao.findAll().subList(0, NO_OF_CLASS_ROOMS);


        List<ClassType> classTypes = new ArrayList<>();
        for (String typeName : CLASS_TYPE_TO_LESSON_KIND.keySet()) {
            Optional<ClassType> ctOpt = classTypeDao.findByName(typeName);
            if (!ctOpt.isPresent()) {
                throw new IllegalStateException("Не известный тип занятий: " + typeName);
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
                    System.err.println("[ошибка] " + e.getMessage());
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
            throw new IllegalStateException("День недели не найден в базе: " + weekAlias);
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
            String msg = String.format("[ошибка] Не существует занятия с таким номером: %d%n", lessonItem.getIndex() + 1);
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
        LecturerSubject lecSubj;

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
                        "Некорректные данные для построяения модели: не сеществует преподавателя '%s', который ведет предмет '%s' (%s.)",
                        lecturer.getInitials(),
                        subject.getName(),
                        classType.getName().substring(0, 3)
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
            System.out.printf("Расписание для группы: %s%n%n", groupRepr(scheduleEntry.getKey()));
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