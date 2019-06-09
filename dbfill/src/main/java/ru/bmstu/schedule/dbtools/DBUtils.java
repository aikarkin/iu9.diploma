package ru.bmstu.schedule.dbtools;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import ru.bmstu.schedule.csv.*;
import ru.bmstu.schedule.csv.LecturerSubjectEntry.ClassKind;
import ru.bmstu.schedule.csv.header.DepartmentHeader;
import ru.bmstu.schedule.csv.header.GroupHeader;
import ru.bmstu.schedule.csv.header.LecturerSubjectsHeader;
import ru.bmstu.schedule.csv.header.SpecializationHeader;
import ru.bmstu.schedule.csv.parser.*;
import ru.bmstu.schedule.dao.*;
import ru.bmstu.schedule.entity.Calendar;
import ru.bmstu.schedule.entity.*;
import ru.bmstu.schedule.html.node.GroupNode;
import ru.bmstu.schedule.html.node.ScheduleItemParityNode;
import ru.bmstu.schedule.html.parser.ScheduleParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DBUtils {

    public static void fillCalendars(SessionFactory factory, String path) {
        File dir = new File(path);
        Pattern cfnPtr = Pattern.compile("(\\p{Lu}+\\d+)__(\\d+[.]\\d+[.]\\d+_\\d+)__(\\d{4})[.]csv");
        CalendarDao calendarDao = new CalendarDao(factory);

        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    String fn = file.getName();
                    Matcher cfnMatcher = cfnPtr.matcher(fn);
                    if (cfnMatcher.matches() && cfnMatcher.groupCount() == 3) {
                        String depCipher = cfnMatcher.group(1);
                        String specCode = cfnMatcher.group(2);
                        int year = Integer.parseInt(cfnMatcher.group(3));
                        System.out.println("Looking for calendar: " + String.format("{year: %d, dep: %s, spec: %s}", year, depCipher, specCode));
                        Optional<Calendar> calendarOpt = calendarDao.findByStartYearAndDepartmentCodeAndSpecCode(year, depCipher, specCode);
                        String csvFile = file.getAbsolutePath();

                        if (calendarOpt.isPresent()) {
                            System.out.println("[info] Fill calendar from file: " + csvFile);
                            try {
                                CSVUtils.fillCalendar(calendarOpt.get(), factory, csvFile);
                            } catch (IOException e) {
                                System.out.println("[error] Failed to parse file: " + csvFile + ". Error message: " + e.getMessage());
                            }
                        } else {
                            System.out.println("[warn] Calendar was not found for file: " + csvFile + ". Skipping it.");
                        }
                    }
                }
            }
        } else {
            System.out.println("[error] Invalid directory path: " + path);
        }
    }

    public static void fillClassRooms(SessionFactory sessionFactory, String roomsRef) throws IOException {
        CSVUtils.fillFromCsv(new ClassroomDao(sessionFactory), roomsRef);
    }


    public static void fillSpecializationsAndDegrees(SessionFactory sessionFactory, String path) throws IOException {
        File refFile = new File(path);

        SpecialityDao specialityDao = new SpecialityDao(sessionFactory);
        EduDegreeDao degreeDao = new EduDegreeDao(sessionFactory);
        SpecializationDao specializationDao = new SpecializationDao(sessionFactory);

        CSVParser records = CSVFormat.EXCEL.withHeader().parse(new FileReader(refFile));
        EntryParser<SpecializationEntry, SpecializationHeader> parser = new SpecializationParser();

        for (CSVRecord record : records) {
            RecordHolder<SpecializationHeader> holder = new RecordHolder<>(record);
            SpecializationEntry specEntry = parser.parse(holder);

            String degreeName = specEntry.getDegreeName();
            Optional<EduDegree> degreeOpt = degreeDao.findByName(degreeName);
            EduDegree degree;

            String specialityCode = specEntry.getSpecialityCode();
            Optional<Speciality> specialityOpt = specialityDao.findByCode(specialityCode);
            Speciality speciality;

            if (!degreeOpt.isPresent()) {
                degree = new EduDegree();
                degree.setName(degreeName);
                degree.setMinNumberOfStudyYears(specEntry.getDegreeStudyYears());
                Integer id = degreeDao.create(degree);
                degree.setId(id);
            } else {
                degree = degreeOpt.get();
            }

            if (!specialityOpt.isPresent()) {
                speciality = new Speciality();
                speciality.setCode(specialityCode);
                speciality.setDegree(degree);
                speciality.setTitle(specEntry.getSpecialityName());
                Integer id = specialityDao.create(speciality);
                speciality.setId(id);
            } else {
                speciality = specialityOpt.get();
            }

            Specialization specialization = new Specialization();
            specialization.setNumberInSpeciality(specEntry.getNumberInSpeciality());
            specialization.setTitle(specEntry.getSpecializationName());
            specialization.setSpeciality(speciality);

            specializationDao.create(specialization);
        }

    }

    public static void fillFaculties(SessionFactory sessionFactory, String refPath) throws IOException {
        CSVUtils.fillFromCsv(new FacultyDao(sessionFactory), refPath);
    }


    public static void fillDepartments(SessionFactory sessionFactory, String refPath) throws IOException {
        DepartmentDao deptDao = new DepartmentDao(sessionFactory);
        FacultyDao facultyDao = new FacultyDao(sessionFactory);

        CSVParser records = CSVFormat.EXCEL.withHeader().parse(new FileReader(new File(refPath)));
        EntryParser<DepartmentEntry, DepartmentHeader> parser = new DepartmentParser();

        for (CSVRecord record : records) {
            RecordHolder<DepartmentHeader> holder = new RecordHolder<>(record);
            DepartmentEntry deptEntry = parser.parse(holder);
            Department dept = new Department();
            Optional<Faculty> factOpt = facultyDao.findByCipher(deptEntry.getFacultyCipher());
            if (!factOpt.isPresent()) {
                System.out.println("[error] No faculty found with cipher: " + deptEntry.getFacultyCipher());
                continue;
            }

            dept.setFaculty(factOpt.get());
            dept.setNumber(deptEntry.getDepartmentNumber());
            dept.setTitle(deptEntry.getDepartmentTitle());

            deptDao.create(dept);
        }
    }

    public static void fillGroups(SessionFactory sessionFactory, String refPath) throws IOException {
        DepartmentSpecializationDao deptSpecDao = new DepartmentSpecializationDao(sessionFactory);
        DepartmentDao deptDao = new DepartmentDao(sessionFactory);
        SpecializationDao specDao = new SpecializationDao(sessionFactory);
        TermDao termDao = new TermDao(sessionFactory);
        StudyGroupDao groupDao = new StudyGroupDao(sessionFactory);

        CalendarDao calendarDao = new CalendarDao(sessionFactory);
        java.util.Calendar dateCalendarInst = java.util.Calendar.getInstance();
        int curYear = dateCalendarInst.get(java.util.Calendar.YEAR);

        CSVParser records = CSVFormat.EXCEL.withHeader().parse(new FileReader(new File(refPath)));
        EntryParser<GroupEntry, GroupHeader> parser = new GroupParser();

        for (CSVRecord record : records) {
            RecordHolder<GroupHeader> holder = new RecordHolder<>(record);
            GroupEntry groupEntry = parser.parse(holder);

            Optional<Department> deptOpt = deptDao.findByCipher(groupEntry.getDepartmentCipher());
            Optional<Specialization> specOpt = specDao.findByCode(groupEntry.getSpecializationCode());

            if (!deptOpt.isPresent() || !specOpt.isPresent()) {
                System.out.printf(
                        "[error] Failed to create department to specialization mapping: department - %s, specialization - %s%n",
                        groupEntry.getDepartmentCipher(),
                        groupEntry.getSpecializationCode()
                );
                continue;
            }

            Optional<DepartmentSpecialization> deptSpecOpt = deptSpecDao.findByDepartmentAndSpecialization(deptOpt.get(), specOpt.get());

            DepartmentSpecialization deptSpec;
            if (deptSpecOpt.isPresent()) {
                deptSpec = deptSpecOpt.get();
            } else {
                deptSpec = new DepartmentSpecialization();
                deptSpec.setDepartment(deptOpt.get());
                deptSpec.setSpecialization(specOpt.get());
                Integer deptSpecId = deptSpecDao.create(deptSpec);
                deptSpec.setId(deptSpecId);
            }

            int enrollmentYear = curYear - groupEntry.getTermNumber() / 2;
            Optional<Calendar> calendarOpt = calendarDao.findByStartYearAndDepartmentCodeAndSpecCode(
                    enrollmentYear,
                    groupEntry.getDepartmentCipher(),
                    groupEntry.getSpecializationCode()
            );
            Calendar calendar;

            if (calendarOpt.isPresent()) {
                calendar = calendarOpt.get();
            } else {
                calendar = new Calendar();
                calendar.setStartYear(enrollmentYear);
                calendar.setDepartmentSpecialization(deptSpec);
                Integer calendarId = calendarDao.create(calendar);
                calendar.setId(calendarId);
            }

            StudyGroup studyGroup = new StudyGroup();
            Optional<Term> termOpt = termDao.findByNumber(groupEntry.getTermNumber());
            if (termOpt.isPresent()) {
                studyGroup.setTerm(termOpt.get());
            } else {
                Term term = new Term();
                term.setNumber(groupEntry.getTermNumber());
                Integer termId = termDao.create(term);
                term.setId(termId);
                studyGroup.setTerm(term);
            }

            studyGroup.setNumber(groupEntry.getGroupNumber());
            studyGroup.setCalendar(calendar);

            groupDao.create(studyGroup);
        }
    }

    public static void fillLecturerSubjects(SessionFactory sessionFactory, String refPath) throws IOException {
        File refFile = new File(refPath);

        LecturerDao lecDao = new LecturerDao(sessionFactory);
        SubjectDao subjDao = new SubjectDao(sessionFactory);
        DepartmentDao deptDao = new DepartmentDao(sessionFactory);
        ClassTypeDao classTypeDao = new ClassTypeDao(sessionFactory);
        LecturerSubjectDao lecSubjDao = new LecturerSubjectDao(sessionFactory);
        DepartmentSubjectDao deptSubjDao = new DepartmentSubjectDao(sessionFactory);

        CSVParser records = CSVFormat.EXCEL.withHeader().parse(new FileReader(refFile));
        EntryParser<LecturerSubjectEntry, LecturerSubjectsHeader> parser = new LecturerSubjectsParser();

        for (CSVRecord record : records) {
            RecordHolder<LecturerSubjectsHeader> holder = new RecordHolder<>(record);
            LecturerSubjectEntry entry = parser.parse(holder);
            String lecInitials = entry.getLecturer();
            String deptCipher = entry.getDepartment();

            List<Lecturer> foundLecturers = lecDao.findByInitials(lecInitials);
            Optional<Department> deptOpt = deptDao.findByCipher(deptCipher);

            if (foundLecturers.isEmpty() || !deptOpt.isPresent()) {
                System.out.printf("[error] Lecturer or department not found: lecturer - %s, department - %s.%n", lecInitials, deptCipher);
                continue;
            }

            if (foundLecturers.size() > 1) {
                System.out.println("[error] Repeated lecturer initials: " + foundLecturers);
                continue;
            }

            for (Map.Entry<LecturerSubjectEntry.ClassKind, String> subjEntry : entry.getSubjectsOfKind()) {
                Optional<Subject> subjOpt = subjDao.findByName(subjEntry.getValue());
                if (!subjOpt.isPresent()) {
                    System.out.println("[error] No subject found with name: " + subjEntry.getValue());
                    continue;
                }
                Optional<DepartmentSubject> deptSubjOpt = deptSubjDao.findByDepartmentAndSubject(deptOpt.get(), subjOpt.get());

                DepartmentSubject deptSubj;

                if (!deptSubjOpt.isPresent()) {
                    deptSubj = new DepartmentSubject();
                    deptSubj.setDepartment(deptOpt.get());
                    deptSubj.setSubject(subjOpt.get());
                    Integer deptSubjId = deptSubjDao.create(deptSubj);
                    deptSubj.setId(deptSubjId);
                } else {
                    deptSubj = deptSubjOpt.get();
                }

                List<LecturerSubject> lecSubjsList = lecSubjDao.findByLecturerAndDepartmentSubject(foundLecturers.get(0), deptSubj);
                Set<String> classTypes = lecSubjsList
                        .stream()
                        .map(lecSubj -> lecSubj.getClassType().getName().substring(3))
                        .collect(Collectors.toSet());

                Consumer<ClassKind> createLecSubjByKind = (kind) -> {
                    String shortName = ClassKind.shortNameByKind(kind);
                    if (!classTypes.contains(shortName)) {
                        Optional<ClassType> ctOpt = classTypeDao.findByShortName(shortName);
                        if (!ctOpt.isPresent()) {
                            System.out.println("[error] No class type with such short name found: " + shortName);
                            return;
                        }

                        LecturerSubject lecSubj = new LecturerSubject();
                        lecSubj.setClassType(ctOpt.get());
                        lecSubj.setLecturer(foundLecturers.get(0));
                        lecSubj.setDepartmentSubject(deptSubj);
                        Integer lecSubjId = lecSubjDao.create(lecSubj);
                        lecSubj.setId(lecSubjId);
                    }
                };

                if (subjEntry.getKey() == LecturerSubjectEntry.ClassKind.any) {
                    for (ClassKind kind : ClassKind.values()) {
                        if (kind != ClassKind.any) {
                            createLecSubjByKind.accept(kind);
                        }
                    }
                } else {
                    createLecSubjByKind.accept(subjEntry.getKey());
                }
            }

        }
    }
}