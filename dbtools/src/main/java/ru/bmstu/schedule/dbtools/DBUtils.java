package ru.bmstu.schedule.dbtools;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import ru.bmstu.schedule.csv.parser.Parser;
import ru.bmstu.schedule.csv.parser.ParserFactory;
import ru.bmstu.schedule.csv.RecordHolder;
import ru.bmstu.schedule.csv.property.DepartmentProperty;
import ru.bmstu.schedule.csv.property.SpecProperty;
import ru.bmstu.schedule.csv.property.SpecToDepartmentsProperty;
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
            String cipher = rec.get(DepartmentProperty.cipher);
            cipherToDep.put(cipher, parsed);
        }

        return cipherToDep;
    }

    public static void fillSpecializations(SessionFactory sessionFactory, String csvFile) throws IOException {
        EduDegreeDao dao = new EduDegreeDao(sessionFactory);
        fillFromCsv(Specialization.class, sessionFactory, csvFile, (entity, rec) -> {
            String degreeName = rec.get(SpecProperty.degree);
            Optional<EduDegree> degree = dao.findByName(degreeName);
            degree.ifPresent(entity::setEduDegree);
        });
    }

    public void fillDepToSpecMapping(SessionFactory sessionFactory, String csvFile) throws IOException {
        SpecializationDao specDao = new SpecializationDao(sessionFactory);
        DepartmentDao depDao = new DepartmentDao(sessionFactory);
        CSVParser parser = CSVFormat.EXCEL.withHeader().parse(new FileReader(csvFile));

        for(CSVRecord rec : parser) {
            RecordHolder holder = new RecordHolder(rec);
            String specCode = holder.get(SpecToDepartmentsProperty.specCode);
            String[] depCodes = holder.get(SpecToDepartmentsProperty.departments).split(";");

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
            String specCode = rec.get(SpecToDepartmentsProperty.specCode);
            String[] departments = rec.get(SpecToDepartmentsProperty.departments).split(";");
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
}
