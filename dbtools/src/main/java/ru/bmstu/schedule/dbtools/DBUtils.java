package ru.bmstu.schedule.dbtools;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import ru.bmstu.schedule.csv.header.SpecToDepHeader;
import ru.bmstu.schedule.csv.parser.Parser;
import ru.bmstu.schedule.csv.parser.ParserFactory;
import ru.bmstu.schedule.csv.RecordHolder;
import ru.bmstu.schedule.csv.header.DepartmentHeader;
import ru.bmstu.schedule.csv.header.SpecHeader;
import ru.bmstu.schedule.csv.header.SpecToDepHeader;
import ru.bmstu.schedule.dao.*;
import ru.bmstu.schedule.entity.*;
import ru.bmstu.schedule.html.parser.ScheduleParser;

import ru.bmstu.schedule.html.node.*;

import java.io.*;
import java.util.*;

import static ru.bmstu.schedule.csv.CSVUtils.fillFromCsv;

public class DBUtils {
    public static void fillClassTime(SessionFactory sessionFactory, ScheduleParser scheduleParser) {
        final LinkedHashSet<ScheduleItemNode> classesTime = new LinkedHashSet<>();
        ClassTimeDao dao = new ClassTimeDao(sessionFactory);
        for(GroupNode group : scheduleParser.getAllGroups()) {
            try {
                for (ScheduleDayNode day : scheduleParser.scheduleFor(group)) {
                    classesTime.addAll(day.getChildren());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int noOfClass = 1;

        for(ScheduleItemNode node : classesTime) {
            EntityAdapter<ClassTime> adapter = EntityAdapter.adapterFor(ClassTime.class, node);
            ClassTime ct = adapter.getEntity();
            ct.setNoOfClass(noOfClass++);

            dao.create(ct);
        }
    }

    public static void fillTerms(SessionFactory sessionFactory, ScheduleParser scheduleParser) {
        TermDao dao = new TermDao(sessionFactory);
        int maxTerm = 0;

        for(GroupNode g : scheduleParser.getAllGroups()) {
            if(g.getTermNumber() > maxTerm)
                maxTerm = g.getTermNumber();
        }

        if(maxTerm == 0)
            maxTerm = 14;

        for (int i = 1; i <= maxTerm + 1; i++) {
            Term term = new Term();
            term.setNumber(i);
            dao.create(term);
        }
    }

    public static void fillClassRooms(SessionFactory sessionFactory, ScheduleParser scheduleParser) throws IOException {
        ClassroomDao dao = new ClassroomDao(sessionFactory);
        Set<String> roomsSet = new HashSet<>();

        for(GroupNode g : scheduleParser.getAllGroups()) {
            for(ScheduleItemParityNode itemParity : scheduleParser.scheduleTravellerFor(g).entitiesListOf(ScheduleItemParityNode.class)) {
                if(StringUtils.isNotEmpty(itemParity.getClassroom())) {
                    String[] classrooms = itemParity.getClassroom().split(",");
                    for (int i = 0; i < classrooms.length; i++) {
                        String crNum = classrooms[i].trim();
                        if(!roomsSet.contains(crNum)) {
                            roomsSet.add(classrooms[i].trim());
                            Classroom classroom = new Classroom();
                            classroom.setRoomNumber(crNum);
                            dao.create(classroom);
                        }
                    }
                }
            }
        }
    }

    public static void fillFacultiesAndDepartments(SessionFactory sessionFactory, String csvFile, ScheduleParser scheduleParser) throws IOException {
        FacultyDao facultyDao = new FacultyDao(sessionFactory);
        Map<String, Department> grCipherToDepartment = loadGroupCipherToDepartmentMapping(csvFile);

        for(FacultyNode facNode : scheduleParser.getFaculties()) {
            EntityAdapter<Faculty> factAdapter = EntityAdapter.adapterFor(Faculty.class, facNode);
            Faculty faculty = factAdapter.getEntity();
            for(DepartmentNode depNode : facNode.getChildren()) {
                Department dep = grCipherToDepartment.get(depNode.getCipher());
                if(dep != null) {
                    faculty.addDepartment(dep);
                } else {
                    System.out.println("noy found cipher: " + depNode.getCipher()) ;
                }
            }
            System.out.println("-> Saving faculty: " + faculty.toString() );
            facultyDao.create(faculty);
        }
    }

    public static Map<String, Department> loadGroupCipherToDepartmentMapping(String csvFile) throws IOException {
        Map<String, Department> cipherToDep = new HashMap<>();
        Parser<Department> entityParser = ParserFactory.parserFor(Department.class);
        CSVParser csvParser = CSVFormat.EXCEL.withHeader().parse(new FileReader(csvFile));
        for(CSVRecord rec : csvParser) {
            Department parsed = entityParser.parse(new RecordHolder(rec));
            String cipher = rec.get(DepartmentHeader.cipher);
            cipherToDep.put(cipher, parsed);
        }

        return cipherToDep;
    }

    public static void fillSpecializations(SessionFactory sessionFactory, String csvFile) throws IOException {
        EduDegreeDao degreeDao = new EduDegreeDao(sessionFactory);
        fillFromCsv(Specialization.class, new SpecializationDao(sessionFactory), csvFile, (entity, rec) -> {
            String degreeName = rec.get(SpecHeader.degree);
            Optional<EduDegree> degree = degreeDao.findByName(degreeName);

            degree.ifPresent(entity::setEduDegree);
        });
    }

    public void fillDepToSpecMapping(SessionFactory sessionFactory, String csvFile) throws IOException {
        SpecializationDao specDao = new SpecializationDao(sessionFactory);
        DepartmentDao depDao = new DepartmentDao(sessionFactory);
        CSVParser parser = CSVFormat.EXCEL.withHeader().parse(new FileReader(csvFile));

        for(CSVRecord rec : parser) {
            RecordHolder holder = new RecordHolder(rec);
            String specCode = holder.get(SpecToDepHeader.specCode);
            String[] depCodes = holder.get(SpecToDepHeader.departments).split(";");

            Optional<Specialization> spec = specDao.findByCode(specCode);

            if(spec.isPresent()) {
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

    public static void fillDepToSpec(SessionFactory sessionFactory, String csvFile) throws IOException {
        CSVParser parser = CSVFormat.EXCEL.withHeader().parse(new FileReader(csvFile));
        SpecializationDao specDao = new SpecializationDao(sessionFactory);
        DepartmentDao depDao = new DepartmentDao(sessionFactory);

        for(CSVRecord rec : parser) {
            String specCode = rec.get(SpecToDepHeader.specCode);
            String[] departments = rec.get(SpecToDepHeader.departments).split(";");
            Optional<Specialization> spec = specDao.findByCode(specCode);

            if(spec.isPresent()) {
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
        System.out.println("!!! year: " + year);

        DepartmentDao depDao = new DepartmentDao(sessionFactory);

        for(DepartmentNode depNode : scheduleParser.getAllDepartments()) {
            Optional<Department> depOpt = depDao.findByCipher(depNode.getCipher());
            if(depOpt.isPresent()) {
                Department dep = depOpt.get();
                for(CourseNode courseNode : depNode.getChildren()) {
                    int noOfCourse = courseNode.getCourseNumber();
                    int enrollment = enrollmentForFirst - noOfCourse + 1;
                    System.out.println(" -> study flow year: " + enrollment);
                    for(Specialization spec : dep.getSpecializations()) {
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

}
