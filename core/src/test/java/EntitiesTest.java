import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.bmstu.schedule.entity.*;
import ru.bmstu.schedule.repository.Repository;


import java.io.FileReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;


public class EntitiesTest {
    private static SessionFactory sessionFactory;

    @SuppressWarnings("Duplicates")
    private static void doInTransaction(Consumer<Session> action) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        action.accept(session);

        session.getTransaction().commit();
        session.close();
    }

    @BeforeAll
    public static void beforeAllTestsStarted() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        doInTransaction(session -> {
            session.createQuery("delete from Faculty");
            session.createQuery("delete from Department");
        });
    }

    @AfterAll
    public static void afterAllTestsFinished() {
        sessionFactory.close();
    }

    @Test
    public void testFaculty() {
        final List<String> facultyNames = Arrays.asList(
                "Информатика и системы урпавления",
                "Энергомашиностроение"
        );

        doInTransaction(session ->
            session.createCriteria(Faculty.class)
                    .list()
                    .forEach(session::delete)
        );

        doInTransaction(session -> {
            assertEquals(session.createCriteria(Faculty.class).list().size(), 0);
        });

        doInTransaction(session -> {
            Faculty faculty1 = new Faculty("ИУ", facultyNames.get(0));
            Faculty faculty2 = new Faculty("Э", facultyNames.get(1));
            Integer saved = (Integer) session.save(faculty1);
            System.out.println(saved);
            session.save(faculty2);
        });

        doInTransaction(session -> {
            List faculties = session.createCriteria(Faculty.class).list();
            HashSet<String> facultiesSet = new HashSet<>(facultyNames);
            for (Object obj : faculties) {
                assertTrue(obj instanceof Faculty);
                Faculty curFact = (Faculty) obj;
                assertTrue(facultiesSet.contains(curFact.getTitle()));
                System.out.println("Faculty: " + curFact.getTitle());
            }
        });
    }

    @Test
    public void testChildDepartments() {

        doInTransaction(session -> {
            Faculty fact = (Faculty) session.createCriteria(Faculty.class).list().get(0);

            fact.addDepartment(new Department(9, "Теоретическая информатика и компьютерные науки"));
            fact.addDepartment(new Department(8, "Компьютерная безопасность"));

            session.save(fact);
        });

        doInTransaction(session -> {
            List departments = session.createCriteria(Department.class).list();
            assertEquals(departments.size(), 2);
            for (Object dep : departments) {
                System.out.println("Faculty: " + ((Department)dep).getFaculty().getTitle());
                System.out.println("\t-> department: " + ((Department)dep).getTitle());
            }
        });
    }

    @Test
    public void testRepository() {
        Repository<Faculty, Integer> facultyRepository = new Repository<>(Faculty.class, sessionFactory);

        // read all
        List<Faculty> allFaculties = facultyRepository.getAll();
        for(Faculty fact : allFaculties) {
            System.out.println(fact.getId() + ": " + fact.getCipher() + " - " + fact.getTitle());
        }

        facultyRepository.findById(1);

        // read first
        Faculty firstFact = facultyRepository.getFirst();
        System.out.println("First faculty: " + firstFact.getCipher());
        assertEquals(firstFact.getCipher(), allFaculties.get(0).getCipher());

        // find by tag
        Faculty factByTag = facultyRepository.findExactByProperty("cipher", "Э");
        System.out.println("Fact by tag: " + factByTag.getCipher());
        assertEquals(factByTag.getCipher(), "Э");

        // create
        facultyRepository.create(new Faculty("МТ", "Машиностроение"));

        assertEquals(facultyRepository.getAll().size(), 3);

        for(Faculty fact : facultyRepository.getAll()) {
            System.out.println(fact.getId() + ": " + fact.getCipher() + " - " + fact.getTitle());
        }

        Faculty lastAdded = facultyRepository.findExactByProperty("cipher", "МТ");

        assertEquals(lastAdded.getCipher(), "МТ");

        facultyRepository.delete(lastAdded);


        // update
        Faculty iuFact = facultyRepository.findExactByProperty("cipher", "ИУ");
        iuFact.setTitle("Informatics and control systems");
        iuFact.setCipher("IU");

        System.out.println("UPDATE");
        facultyRepository.update(iuFact);
        System.out.println("UPDATE END");


        assertEquals(facultyRepository.findExactByProperty("cipher", "IU").getTitle(), "Informatics and control systems");
        assertEquals(facultyRepository.getAll().size(), 2);
    }

    @Test
    public void testDepartmentsAndSpecializationFetching() {
        Repository<Specialization, Integer> specRep = new Repository<>(Specialization.class, sessionFactory);
        Specialization spec1 = specRep.findExactByProperty("code", "01.03.02");
        Repository<Department, Integer> depRep = new Repository<>(Department.class, sessionFactory);
        List<Department> иу9 = depRep.filter(dep -> dep.getCipher().equals("ИУ9"));
    }

    @Test
    public void updateIfSpecializationCodeEquals38_03_02() {
        Repository<Specialization, Integer> specRep = new Repository<>(Specialization.class, sessionFactory);
        Repository<Department, Integer> depRep = new Repository<>(Department.class, sessionFactory);
        Specialization spec = specRep.findById(1196);

        Department dep = depRep.findById(826);
        System.out.println("dep: " + dep);
        spec.addDepartment(dep);

        specRep.update(spec);


    }

    @Test
    public void testDepartmentsAndSpecializations() {
        Repository<Specialization, Integer> specRep = new Repository<>(Specialization.class, sessionFactory);
        Repository<Department, Integer> depRep = new Repository<>(Department.class, sessionFactory);

        Specialization spec1 = specRep.findExactByProperty("code", "01.03.02");
        Department dep1 = depRep.findById(19); // IU9
        Department dep2 = depRep.findById(18); // IU8

        dep1.addSpecialization(spec1); // IU9: 01.03.02;    01.03.02: IU8
        spec1.addDepartment(dep2); //     01.03.02: IU8;    IU8: 01.03.02

        specRep.update(spec1);
        depRep.update(dep1);

        dep1 = depRep.findById(19);
        dep2 = depRep.findById(18);

        System.out.printf("Departments of specialization '%s'%n", spec1.getCode());
        for(Department dep : spec1.getDepartments()) {
            System.out.println(dep);
        }

        System.out.println("------------------");

        System.out.printf("Specializations of department '%s'%n", dep1.getCipher());
        for(Specialization spec : dep1.getSpecializations()) {
            System.out.println(spec);
        }

        System.out.println("------------------");

        System.out.printf("Specializations of department '%s'%n", dep2.getCipher());
        for(Specialization spec : dep2.getSpecializations()) {
            System.out.println(spec);
        }
    }

    @Test
    public void testStudyFlows() {
        Repository<Specialization, Integer> specRep = new Repository<>(Specialization.class, sessionFactory);
        Repository<Department, Integer> depRep = new Repository<>(Department.class, sessionFactory);

        Specialization spec = specRep.findById(1);
        Department dep = depRep.findById(19); // iu9
        StudyFlow flow1 = new StudyFlow();
        flow1.setEnrollmentYear(2015);

        dep.addStudyFlow(flow1, spec);

        depRep.update(dep);

        StudyFlow flow2 = new StudyFlow();
        flow2.setEnrollmentYear(2014);
        spec.addStudyFlow(flow2, dep);

        specRep.update(spec);

        System.out.printf("Study flows of department %s:%n", dep.getCipher());
        for(StudyFlow flow : dep.getStudyFlows()) {
            System.out.println(flow.getEnrollmentYear());
        }

        System.out.println("--------------");

        System.out.printf("Study flows of specialization %s%n", spec.getStudyFlows());
        for(StudyFlow flow : spec.getStudyFlows()) {
            System.out.println(flow.getEnrollmentYear());
        }
    }

    @Test
    public void fetchDepToSpec() {
        Repository<Specialization, Integer> specRep = new Repository<>(Specialization.class, sessionFactory);
        Specialization spec = specRep.findExactByProperty("code", "38.03.02");

        System.out.printf("Departments of specialization '%s':%n", spec);

        for (Department dep : spec.getDepartments()) {
            System.out.println("--> " + dep);
        }
    }
}
