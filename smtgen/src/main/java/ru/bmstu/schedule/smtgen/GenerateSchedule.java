package ru.bmstu.schedule.smtgen;

import org.apache.commons.cli.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.bmstu.schedule.dao.*;
import ru.bmstu.schedule.entity.Calendar;
import ru.bmstu.schedule.entity.DayOfWeek;
import ru.bmstu.schedule.entity.*;

import java.util.*;

public class GenerateSchedule {

    private static final int NO_OF_STUDY_WEEKS = 17;
    private static final String PARITY_ALWAYS = "ЧС/ЗН";
    private static final String PARITY_NUM = "ЧС";
    private static final String PARITY_DEN = "ЗН";

    private static final Map<String, LessonKind> CLASS_TYPE_TO_LESSON_KIND;
    private static SessionFactory sessionFactory;
    private static List<String> requiredOptionsForSpec = new ArrayList<>();
    private static CommandLine cmd;

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
        try {
            initCmdOptions(args);
            checkArgs(cmd);
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

    private void checkAllGroups(List<StudyGroup> groups) {
        if (groups.size() == 0)
            return;

        Calendar firstCalendar = groups.get(0).getCalendar();
        for (int i = 1; i < groups.size(); i++) {
            if (!groups.get(i).getCalendar().equals(firstCalendar)) {
                throw new RuntimeException("Unable to generate schedule: Provided groups has different calendar plan");
            }
        }
    }

    private Map<StudyGroup, Schedule> generateSchedules() throws RuntimeException {
        List<StudyGroup> groups = new ArrayList<>();
        Calendar calendar;
        int term;
        if (cmd.hasOption("g")) {
            String[] groupsCiphers = cmd.getOptionValues("g");
            for (String grCipher : groupsCiphers) {
                Optional<StudyGroup> grOpt = studyGroupDao.findByCipher(grCipher);
                if (!grOpt.isPresent()) {
                    throw new RuntimeException("Group with such cipher not found: " + grCipher);
                }
                groups.add(grOpt.get());
            }
            checkAllGroups(groups);
            calendar = groups.get(0).getCalendar();
            term = groups.get(0).getTerm().getNumber();
        } else {
            int year = Integer.valueOf(cmd.getOptionValue("y"));
            term = Integer.valueOf(cmd.getOptionValue("t"));
            String deptCipher = cmd.getOptionValue("d");
            String specCode = cmd.getOptionValue("s");

            Optional<Calendar> calendarOpt = calendarDao.findByStartYearAndDepartmentCodeAndSpecCode(year, deptCipher, specCode);

            if (!calendarOpt.isPresent()) {
                throw new RuntimeException("Failed to find calendar with such parameters.");
            }
            calendar = calendarOpt.get();

            for (StudyGroup group : calendar.getStudyGroups()) {
                if (group.getTerm().getNumber() == term) {
                    groups.add(group);
                }
            }
        }

        Map<Subject, SubjectsPerWeek> subjectsPerWeekMap = new HashMap<>();
        List<Classroom> classrooms = new ArrayList<>();
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

    private static void initCmdOptions(String[] args) throws ParseException {
        Options cmdOptions = new Options();

        Option group = new Option("g", "List of groups ciphers");
        group.setArgs(Option.UNLIMITED_VALUES);
        group.setLongOpt("groups");

        Option specCode = new Option("s", "Specialization code");
        specCode.setArgs(1);
        specCode.setType(String.class);

        Option deptCipher = new Option("d", "Department cipher");
        deptCipher.setArgs(1);
        deptCipher.setType(String.class);

        Option term = new Option("t", "Term number");
        term.setArgs(1);
        term.setType(Integer.class);

        Option year = new Option("y", "Groups enrollment year");
        year.setArgs(1);
        term.setType(Integer.class);

        cmdOptions.addOption(group);
        cmdOptions.addOption(specCode);
        cmdOptions.addOption(term);
        cmdOptions.addOption(year);
        cmdOptions.addOption(deptCipher);
        cmdOptions.addOption(
                Option.builder("h")
                        .longOpt("help")
                        .desc("Print help message")
                        .hasArg(false)
                        .build()
        );

        Collections.addAll(requiredOptionsForSpec, "s", "d", "t", "y");

        cmd = new DefaultParser().parse(cmdOptions, args);
    }

    private static void checkArgs(CommandLine cmd) throws RuntimeException {
        boolean hasAllSpecOptions = requiredOptionsForSpec.stream().allMatch(cmd::hasOption);

        if (!cmd.hasOption("g") && !hasAllSpecOptions) {
            throw new RuntimeException("Invalid options: you should specify neither groups list (-g) or year (-y), term (-t), specialization (-s), department (-d)");
        }
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

    private static final String[] rooms = new String[]{
            "330аю",
            "739л",
            "831л",
            "830л",
            "615л",
            "717л",
            "1015л",
            "1031л",
            "309ю",
            "501ю",
    };

}