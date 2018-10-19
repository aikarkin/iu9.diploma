//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.cfg.Configuration;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import ru.bmstu.schedule.entity.Department;
//import ru.bmstu.schedule.entity.Faculty;
//import ru.bmstu.schedule.repository.Repository;
//
//
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//import java.util.function.Consumer;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//
//public class EntitiesTest {
//    private static SessionFactory sessionFactory;
//
//    @SuppressWarnings("Duplicates")
//    private static void doInTransaction(Consumer<Session> action) {
//        Session session = sessionFactory.openSession();
//        session.beginTransaction();
//
//        action.accept(session);
//
//        session.getTransaction().commit();
//        session.close();
//    }
//
//    @BeforeClass
//    public static void beforeAllTestsStarted() {
//        sessionFactory = new Configuration().configure().buildSessionFactory();
//        doInTransaction(session -> {
//            session.createQuery("delete from Faculty");
//            session.createQuery("delete from Department");
//        });
//    }
//
//    @AfterClass
//    public static void afterAllTestsFinished() {
//        sessionFactory.close();
//    }
//
//    @Test
//    public void testFaculty() {
//        final List<String> facultyNames = Arrays.asList(
//                "Информатика и системы урпавления",
//                "Энергомашиностроение"
//        );
//
//        doInTransaction(session ->
//            session.createCriteria(Faculty.class)
//                    .list()
//                    .forEach(session::delete)
//        );
//
//        doInTransaction(session -> {
//            assertEquals(session.createCriteria(Faculty.class).list().size(), 0);
//        });
//
//        doInTransaction(session -> {
//            Faculty faculty1 = new Faculty("ИУ", facultyNames.get(0));
//            Faculty faculty2 = new Faculty("Э", facultyNames.get(1));
//            Integer saved = (Integer) session.save(faculty1);
//            System.out.println(saved);
//            session.save(faculty2);
//        });
//
//        doInTransaction(session -> {
//            List faculties = session.createCriteria(Faculty.class).list();
//            HashSet<String> facultiesSet = new HashSet<>(facultyNames);
//            for (Object obj : faculties) {
//                assertTrue(obj instanceof Faculty);
//                Faculty curFact = (Faculty) obj;
//                assertTrue(facultiesSet.contains(curFact.getTitle()));
//                System.out.println("Faculty: " + curFact.getTitle());
//            }
//        });
//    }
//
//    @Test
//    public void testChildDepartments() {
//
//        doInTransaction(session -> {
//            Faculty fact = (Faculty) session.createCriteria(Faculty.class).list().get(0);
//
//            fact.addDepartment(new Department(9, "Теоретическая информатика и компьютерные науки"));
//            fact.addDepartment(new Department(8, "Компьютерная безопасность"));
//
//            session.save(fact);
//        });
//
//        doInTransaction(session -> {
//            List departments = session.createCriteria(Department.class).list();
//            assertEquals(departments.size(), 2);
//            for (Object dep : departments) {
//                System.out.println("Faculty: " + ((Department)dep).getFaculty().getTitle());
//                System.out.println("\t-> department: " + ((Department)dep).getTitle());
//            }
//        });
//    }
//
//    @Test
//    public void testRepository() {
//        Repository<Faculty, Integer> facultyRepository = new Repository<>(Faculty.class, sessionFactory);
//
//        // read all
//        List<Faculty> allFaculties = facultyRepository.getAll();
//        for(Faculty fact : allFaculties) {
//            System.out.println(fact.getId() + ": " + fact.getCipher() + " - " + fact.getTitle());
//        }
//
//        facultyRepository.findById(1);
//
//        // read first
//        Faculty firstFact = facultyRepository.getFirst();
//        System.out.println("First faculty: " + firstFact.getCipher());
//        assertEquals(firstFact.getCipher(), allFaculties.get(0).getCipher());
//
//        // find by tag
//        Faculty factByTag = facultyRepository.findExactByProperty("cipher", "Э");
//        System.out.println("Fact by tag: " + factByTag.getCipher());
//        assertEquals(factByTag.getCipher(), "Э");
//
//        // create
//        facultyRepository.create(new Faculty("МТ", "Машиностроение"));
//
//        assertEquals(facultyRepository.getAll().size(), 3);
//
//        for(Faculty fact : facultyRepository.getAll()) {
//            System.out.println(fact.getId() + ": " + fact.getCipher() + " - " + fact.getTitle());
//        }
//
//        Faculty lastAdded = facultyRepository.findExactByProperty("cipher", "МТ");
//
//        assertEquals(lastAdded.getCipher(), "МТ");
//
//        facultyRepository.delete(lastAdded);
//
//
//        // update
//        Faculty iuFact = facultyRepository.findExactByProperty("cipher", "ИУ");
//        iuFact.setTitle("Informatics and control systems");
//        iuFact.setCipher("IU");
//
//        System.out.println("UPDATE");
//        Faculty updated = facultyRepository.update(iuFact);
//        System.out.println("UPDATE END");
//
//        assertEquals(updated.getCipher(), "IU");
//        assertEquals(facultyRepository.findExactByProperty("cipher", "IU").getTitle(), "Informatics and control systems");
//        assertEquals(facultyRepository.getAll().size(), 2);
//    }
//}
