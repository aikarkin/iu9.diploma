package ru.bmstu.schedule.dbtools;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import ru.bmstu.schedule.csv.CSVUtils;
import ru.bmstu.schedule.csv.RecordHolder;
import ru.bmstu.schedule.csv.header.DepartmentHeader;
import ru.bmstu.schedule.csv.header.SpecHeader;
import ru.bmstu.schedule.csv.header.SpecToDepHeader;
import ru.bmstu.schedule.csv.parser.Parser;
import ru.bmstu.schedule.csv.parser.ParserFactory;
import ru.bmstu.schedule.dao.*;
import ru.bmstu.schedule.dbtools.converter.GroupConverter;
import ru.bmstu.schedule.dbtools.converter.ScheduleDayConverter;
import ru.bmstu.schedule.entity.*;
import ru.bmstu.schedule.html.node.*;
import ru.bmstu.schedule.html.parser.ScheduleParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static ru.bmstu.schedule.csv.CSVUtils.fillFromCsv;

public class DBUtils {

    public static void fillClassTime(SessionFactory sessionFactory, ScheduleParser scheduleParser) {
        final LinkedHashSet<ScheduleItemNode> classesTime = new LinkedHashSet<>();
        ClassTimeDao dao = new ClassTimeDao(sessionFactory);
        for (GroupNode group : scheduleParser.getAllGroups()) {
            try {
                for (ScheduleDayNode day : scheduleParser.scheduleFor(group)) {
                    classesTime.addAll(day.getChildren());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int noOfClass = 1;

        for (ScheduleItemNode node : classesTime) {
            EntityAdapter<ClassTime> adapter = EntityAdapter.adapterFor(ClassTime.class, node);
            ClassTime ct = adapter.getEntity();
            ct.setNoOfClass(noOfClass++);

            dao.create(ct);
        }
    }

    public static void fillTerms(SessionFactory sessionFactory, ScheduleParser scheduleParser) {
        TermDao dao = new TermDao(sessionFactory);
        int maxTerm = 0;

        for (GroupNode g : scheduleParser.getAllGroups()) {
            if (g.getTermNumber() > maxTerm)
                maxTerm = g.getTermNumber();
        }

        if (maxTerm == 0)
            maxTerm = 14;

        for (int i = 1; i <= maxTerm + 1; i++) {
            Term term = new Term();
            term.setNumber(i);
            dao.create(term);
        }
    }

    public static void fillClassRooms(SessionFactory sessionFactory, ScheduleParser scheduleParser) {
        ClassroomDao dao = new ClassroomDao(sessionFactory);
        Set<String> roomsSet = new HashSet<>();

        for (GroupNode g : scheduleParser.getAllGroups()) {
            try {
                for (ScheduleItemParityNode itemParity : scheduleParser.scheduleTravellerFor(g).entitiesListOf(ScheduleItemParityNode.class)) {
                    if (StringUtils.isNotEmpty(itemParity.getClassroom())) {
                        String[] classrooms = itemParity.getClassroom().split(",");
                        for (String classroom1 : classrooms) {
                            String crNum = classroom1.trim();
                            if (!roomsSet.contains(crNum)) {
                                roomsSet.add(classroom1.trim());
                                Classroom classroom = new Classroom();
                                classroom.setRoomNumber(crNum);
                                dao.create(classroom);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.printf("[warn] Failed to fetch schedule info for group: %s%n", g.getCipher());
            }
        }
    }

    public static void fillFacultiesAndDepartments(SessionFactory sessionFactory, String csvFile, ScheduleParser scheduleParser) throws IOException {
        FacultyDao facultyDao = new FacultyDao(sessionFactory);
        DepartmentDao departmentDao = new DepartmentDao(sessionFactory);
//        Map<String, Department> grCipherToDepartment = loadGroupCipherToDepartmentMapping(departmentDao, csvFile);
        Map<String, Department> grCipherToDepartment = loadGroupCipherToDepartmentMapping(csvFile);

        for (FacultyNode facNode : scheduleParser.getFaculties()) {
            EntityAdapter<Faculty> factAdapter = EntityAdapter.adapterFor(Faculty.class, facNode);
            Faculty faculty = factAdapter.getEntity();
            for (DepartmentNode depNode : facNode.getChildren()) {
                Department dep = grCipherToDepartment.get(depNode.getCipher());
                if (dep != null) {
                    faculty.addDepartment(dep);
                } else {
                    System.out.println("[warn] Department with cipher '" + depNode.getCipher() + "' was not found");
                }
            }
            facultyDao.create(faculty);
        }
    }

    public static void fillSpecializations(SessionFactory sessionFactory, String csvFile) throws IOException {
        EduDegreeDao degreeDao = new EduDegreeDao(sessionFactory);
        fillFromCsv(new SpecializationDao(sessionFactory), csvFile, (entity, rec) -> {
            String degreeName = rec.get(SpecHeader.degree);
            Optional<EduDegree> degree = degreeDao.findByName(degreeName);

            degree.ifPresent(entity::setEduDegree);
        });
    }

    public static void fillDepToSpec(SessionFactory sessionFactory, String csvFile) throws IOException {
        CSVParser parser = CSVFormat.EXCEL.withHeader().parse(new FileReader(csvFile));
        SpecializationDao specDao = new SpecializationDao(sessionFactory);
        DepartmentDao depDao = new DepartmentDao(sessionFactory);

        for (CSVRecord rec : parser) {
            String specCode = rec.get(SpecToDepHeader.specCode);
            String[] departments = rec.get(SpecToDepHeader.departments).split(";");
            Optional<Specialization> spec = specDao.findByCode(specCode);

            if (spec.isPresent()) {
                for (String depCipher : departments) {
                    Optional<Department> dep = depDao.findByCipher(depCipher);

                    if (dep.isPresent()) {
                        spec.get().addDepartment(dep.get());
                    } else {
                        System.out.println("[warn] Department with cipher '" + depCipher + "' was not found, skipping record");
                    }
                }
                specDao.update(spec.get());
            } else {
                System.out.println("[warn] Specialization with code '" + specCode + "' is not found, skipping record");
            }

        }
    }

    public static void fillStudyFlows(SessionFactory sessionFactory, ScheduleParser scheduleParser) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR), month = calendar.get(Calendar.MONTH),
                enrollmentForFirst = month >= 9 && month <= 12 ? year : year - 1;

        DepartmentDao depDao = new DepartmentDao(sessionFactory);

        for (DepartmentNode depNode : scheduleParser.getAllDepartments()) {
            Optional<Department> depOpt = depDao.findByCipher(depNode.getCipher());
            if (depOpt.isPresent()) {
                Department dep = depOpt.get();
                for (CourseNode courseNode : depNode.getChildren()) {
                    int noOfCourse = courseNode.getCourseNumber();
                    int enrollment = enrollmentForFirst - noOfCourse + 1;
                    for (Specialization spec : dep.getSpecializations()) {
                        try {
                            StudyFlow flow = new StudyFlow();
                            flow.setEnrollmentYear(enrollment);
                            dep.addStudyFlow(flow, spec);
                        } catch (HibernateException e) {
                            System.out.println("[warn] Failed to create study flow: " + e.getMessage());
                        }
                    }
                    depDao.update(dep);
                }
            }
        }
    }

    public static void fillCalendars(SessionFactory factory, String path) {
        File dir = new File(path);
        Pattern cfnPtr = Pattern.compile("(\\p{Lu}+\\d+)_(\\d+[.]\\d+[.]\\d+)_(\\d{4})[.]csv");
        StudyFlowDao flowDao = new StudyFlowDao(factory);

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
                        System.out.println("Looking for study flow: " + String.format("{year: %d, dep: %s, spec: %s}", year, depCipher, specCode));
                        Optional<StudyFlow> flowOpt = flowDao.findByYearDepartmentAndSpecialization(year, depCipher, specCode);
                        String csvFile = file.getAbsolutePath();

                        if (flowOpt.isPresent()) {
                            System.out.println("[info] Fill calendar from file: " + csvFile);
                            try {
                                CSVUtils.fillCalendar(flowOpt.get(), factory, csvFile);
                            } catch (IOException e) {
                                System.out.println("[error] Failed to parse file: " + csvFile + ". Error message: " + e.getMessage());
                            }
                        } else {
                            System.out.println("[warn] Study flow was not found for file: " + csvFile + ". Skipping it.");
                        }
                    }
                }
            }
        } else {
            System.out.println("[error] Invalid directory path: " + path);
        }
    }

    public static void fillSchedule(SessionFactory factory, final ScheduleParser scheduleParser) {
        DepartmentDao depDao = new DepartmentDao(factory);
        SpecializationDao specDao = new SpecializationDao(factory);
        StudyFlowDao flowDao = new StudyFlowDao(factory);
        StudyGroupDao groupDao = new StudyGroupDao(factory);
        GroupConverter groupConverter = new GroupConverter(factory);

        for (DepartmentNode depNode : scheduleParser.getAllDepartments()) {
            Optional<Department> depOpt = depDao.findByCipher(depNode.getCipher());
            if (depOpt.isPresent()) {
                final Map<String, Integer> subjTitleToSpecId = loadSubjectToSpecIdMap(depOpt.get());

                depNode.getChildren()
                        .stream()
                        .flatMap(c -> c.getChildren().stream())
                        .forEach(grNode -> {
                            StudyGroup group = groupConverter.convert(grNode);
                            try {
                                List<ScheduleDayNode> curSchedule = scheduleParser.scheduleFor(grNode);

                                if (curSchedule.size() > 0) {
                                    System.out.println("[info] Fill schedule for group: " + grNode.getCipher());
                                    for (ScheduleDayNode dayNode : curSchedule) {
                                        if (ScheduleDayConverter.isDayNodeNotEmpty(dayNode)) {
                                            ScheduleDay day = new ScheduleDayConverter(factory).convert(dayNode);
                                            group.addScheduleDay(day);
                                        }
                                    }
                                    int specId = getMostPossibleSpeciality(
                                            subjTitleToSpecId,
                                            curSchedule
                                                    .stream()
                                                    .flatMap(sd -> sd.getChildren().stream())
                                    );
                                    Specialization spec = null;
                                    if (specId == 0) {
                                        Optional<Department> depOptByCipher = depDao.findByCipher(grNode.getParent().getParent().getCipher());
                                        if (depOptByCipher.isPresent()) {
                                            List<Specialization> specs = depOptByCipher.get().getSpecializations();
                                            if (specs.size() > 0)
                                                spec = specs.get(0);
                                        }
                                    } else {
                                        spec = specDao.findByKey(specId);
                                    }

                                    if (spec != null) {
                                        Optional<StudyFlow> parentFlowOpt = flowDao.findByYearDepartmentAndSpecialization(
                                                Calendar.getInstance().get(Calendar.YEAR),
                                                depOpt.get().getCipher(),
                                                spec.getCode()
                                        );
                                        parentFlowOpt.ifPresent(flow -> {
                                            group.setStudyFlow(flow);
                                            groupDao.create(group);
                                        });
                                    } else {
                                        System.out.println("[warn] Group '" + grNode.getCipher() + "' was not created because parent department hasn't matching speciality. Skipping entry");
                                    }
                                } else {
                                    System.out.println("[warn] Schedule for group '" + grNode.getCipher() + "' is absent. Skipping it");
                                }
                            } catch (IOException | IllegalStateException e) {
                                e.printStackTrace();
                                System.out.println("[error] Failed to fetch schedule for group: " + grNode.getCipher());
                            }
                        });
            }
        }
    }

    public void fillDepToSpecMapping(SessionFactory sessionFactory, String csvFile) throws IOException {
        SpecializationDao specDao = new SpecializationDao(sessionFactory);
        DepartmentDao depDao = new DepartmentDao(sessionFactory);
        CSVParser parser = CSVFormat.EXCEL.withHeader().parse(new FileReader(csvFile));

        for (CSVRecord rec : parser) {
            RecordHolder holder = new RecordHolder(rec);
            String specCode = holder.get(SpecToDepHeader.specCode);
            String[] depCodes = holder.get(SpecToDepHeader.departments).split(";");

            Optional<Specialization> spec = specDao.findByCode(specCode);

            if (spec.isPresent()) {
                for (String depCode : depCodes) {
                    Optional<Department> dep = depDao.findByCipher(depCode);
                    dep.ifPresent(department -> spec.get().addDepartment(department));
                }
                specDao.update(spec.get());
            } else {
                System.out.println("[warn] Specialization with code '" + specCode + "' is not found, skipping record");
            }
        }
    }

    static Map<String, Department> loadGroupCipherToDepartmentMapping(String csvFile) throws IOException {
        final Map<String, Department> cipherToDep = new HashMap<>();

//        CSVUtils.fillFromCsv(departmentDao, csvFile, (dep, rec) -> {
//            String cipher = rec.record().get(DepartmentHeader.cipher);
//            cipherToDep.put(cipher, dep);
//        });

        Parser<Department, ?> entityParser = ParserFactory.parserFor(Department.class);
        CSVParser csvParser = CSVFormat.EXCEL.withHeader().parse(new FileReader(csvFile));
        for (CSVRecord rec : csvParser) {
            Department parsed = entityParser.parse(new RecordHolder(rec));
            String cipher = rec.get(DepartmentHeader.cipher);
            cipherToDep.put(cipher, parsed);
        }

        return cipherToDep;
    }

    private static Integer getMostPossibleSpeciality(Map<String, Integer> subjTitleToSpecId, Stream<ScheduleItemNode> scheduleItems) {
        Map<Integer, Integer> specIdToScore = new HashMap<>();

        scheduleItems
                .flatMap(item -> item.getChildren().stream())
                .forEach(parity -> {
                    String subjName = parity.getSubject();
                    if (subjTitleToSpecId.containsKey(subjName)) {
                        int specId = subjTitleToSpecId.get(subjName);
                        if (!specIdToScore.containsKey(specId)) {
                            specIdToScore.put(specId, 0);
                        }
                        specIdToScore.put(specId, specIdToScore.get(specId) + 1);
                    }
                });

        int specId = 0, maxScore = 0;

        for (Map.Entry<Integer, Integer> entry : specIdToScore.entrySet()) {
            if (entry.getValue() > maxScore) {
                maxScore = entry.getValue();
                specId = entry.getKey();
            }

        }

        return specId;
    }

    private static Map<String, Integer> loadSubjectToSpecIdMap(Department dep) {
        Map<String, Integer> retMap = new HashMap<>();

        for (StudyFlow flow : dep.getStudyFlows()) {
            for (CalendarItem ci : flow.getCalendarItems()) {
                retMap.put(ci.getSubject().getName(), flow.getSpecialization().getId());
            }
        }

        return retMap;
    }


}
