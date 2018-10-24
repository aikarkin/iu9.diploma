package ru.bmstu.schedule.dbtools;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import ru.bmstu.schedule.dbtools.csv.Parser;
import ru.bmstu.schedule.dbtools.csv.ParserFactory;
import ru.bmstu.schedule.dbtools.csv.RecordHolder;
import ru.bmstu.schedule.dbtools.csv.property.DepartmentProperty;
import ru.bmstu.schedule.dbtools.csv.property.SpecProperty;
import ru.bmstu.schedule.dbtools.csv.property.SpecToDepartmentsProperty;
import ru.bmstu.schedule.entity.*;
import ru.bmstu.schedule.parser.ScheduleService;

import ru.bmstu.schedule.parser.node.*;
import ru.bmstu.schedule.repository.Repository;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;

public class DBUtils {
    public static <E, K extends Serializable> void
    fillFromCsv(Class<E> entityClass, SessionFactory sessionFactory, String csvFile) throws IOException, NotImplementedException {
        fillFromCsv(entityClass, sessionFactory, csvFile, (e, r) -> {});
    }

    public static <E, K extends Serializable> void
    fillFromCsv(Class<E> entityClass, SessionFactory sessionFactory, String csvFile, BiConsumer<E, RecordHolder> entityConsumer) throws IOException {
        CSVParser parser = CSVFormat.EXCEL.withHeader().parse(new FileReader(csvFile));
        Repository<E, K> repository = new Repository<>(entityClass, sessionFactory);
        Parser<E> entityParser = (Parser<E>) ParserFactory.parserFor(entityClass);

        if (entityParser != null) {
            for(CSVRecord rec : parser) {
                RecordHolder holder = new RecordHolder(rec);
                E parsed = entityParser.parse(holder);
                entityConsumer.accept(parsed, holder);
                repository.create(parsed);
            }
        } else {
            throw new NotImplementedException();
        }
    }

    public static void fillClassTime(SessionFactory sessionFactory, ScheduleService svc) throws IOException {
        final LinkedHashSet<ScheduleItemNode> classesTime = new LinkedHashSet<>();
        Repository<ClassTime, Integer> repository = new Repository<>(ClassTime.class, sessionFactory);

        for(GroupNode group : svc.getAllGroups()) {
            try {
                for (ScheduleDayNode day : svc.scheduleFor(group)) {
                    classesTime.addAll(day.getChildren());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int noOfClass = 1;

        for(ScheduleItemNode node : classesTime) {
            EntityAdapter<ClassTime> adapter = (EntityAdapter<ClassTime>) EntityAdapter.adapterFor(ClassTime.class, node);
            ClassTime ct = adapter.getEntity();
            ct.setNoOfClass(noOfClass++);

            repository.create(ct);
        }
    }

    public static void fillTerms(SessionFactory sessionFactory, ScheduleService svc) {
        Repository<Term, Integer> repository = new Repository<>(Term.class, sessionFactory);
        int maxTerm = 0;

        for(GroupNode g : svc.getAllGroups()) {
            if(g.getTermNumber() > maxTerm)
                maxTerm = g.getTermNumber();
        }

        if(maxTerm == 0)
            maxTerm = 14;

        for (int i = 1; i <= maxTerm + 1; i++) {
            Term term = new Term();
            term.setNumber(i);
            repository.create(term);
        }
    }

    public static void fillClassRooms(SessionFactory sessionFactory, ScheduleService svc) throws IOException {
        Repository<Classroom, Integer> repository = new Repository<>(Classroom.class, sessionFactory);
        Set<String> roomsSet = new HashSet<>();

        for(GroupNode g : svc.getAllGroups()) {
            for(ScheduleItemParityNode itemParity : svc.scheduleTravellerFor(g).entitiesListOf(ScheduleItemParityNode.class)) {
                if(StringUtils.isNotEmpty(itemParity.getClassroom())) {
                    String[] classrooms = itemParity.getClassroom().split(",");
                    for (int i = 0; i < classrooms.length; i++) {
                        String crNum = classrooms[i].trim();
                        if(!roomsSet.contains(crNum)) {
                            roomsSet.add(classrooms[i].trim());
                            Classroom classroom = new Classroom();
                            classroom.setRoomNumber(crNum);
                            repository.create(classroom);
                        }
                    }
                }
            }
        }
    }

    public static void fillFacultiesAndDepartments(SessionFactory sessionFactory, String csvFile, ScheduleService svc) throws IOException {
        Repository<Faculty, Integer> facultyRep = new Repository<>(Faculty.class, sessionFactory);
        Map<String, Department> grCipherToDepartment = loadGroupCipherToDepartmentMapping(csvFile);

        for(FacultyNode facNode : svc.getFaculties()) {
            EntityAdapter<Faculty> factAdapter = (EntityAdapter<Faculty>) EntityAdapter.adapterFor(Faculty.class, facNode);
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
            facultyRep.create(faculty);
        }
    }

    public static Map<String, Department> loadGroupCipherToDepartmentMapping(String csvFile) throws IOException {
        Map<String, Department> cipherToDep = new HashMap<>();
        Parser<Department> entityParser = (Parser<Department>) ParserFactory.parserFor(Department.class);
        CSVParser csvParser = CSVFormat.EXCEL.withHeader().parse(new FileReader(csvFile));
        for(CSVRecord rec : csvParser) {
            Department parsed = entityParser.parse(new RecordHolder(rec));
            String cipher = rec.get(DepartmentProperty.cipher);
            cipherToDep.put(cipher, parsed);
        }

        return cipherToDep;
    }

    public static void fillSpecializations(SessionFactory sessionFactory, String csvFile) throws IOException {
        final Repository<EduDegree, Integer> degreeRep = new Repository<>(EduDegree.class, sessionFactory);
        fillFromCsv(Specialization.class, sessionFactory, csvFile, (entity, rec) -> {
            String degreeName = rec.get(SpecProperty.degree);
            EduDegree degree = degreeRep.findExactByProperty("name", degreeName);
            entity.setEduDegree(degree);
        });
    }

    public void fillDepToSpecMapping(SessionFactory sessionFactory, String csvFile) throws IOException {
        Repository<Specialization, Integer> specRep = new Repository<>(Specialization.class, sessionFactory);
        Repository<Department, Integer> depRep = new Repository<>(Department.class, sessionFactory);
        CSVParser parser = CSVFormat.EXCEL.withHeader().parse(new FileReader(csvFile));

        for(CSVRecord rec : parser) {
            RecordHolder holder = new RecordHolder(rec);
            String specCode = holder.get(SpecToDepartmentsProperty.specCode);
            String[] depCodes = holder.get(SpecToDepartmentsProperty.departments).split(";");

            Specialization spec = specRep.findExactByProperty("code", specCode);

            for(String depCode : depCodes) {
                Department dep = depRep.findExactByProperty("cipher", depCode);
                spec.addDepartment(dep);
            }

            specRep.update(spec);
        }
    }
}
