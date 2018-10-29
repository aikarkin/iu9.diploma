import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.bmstu.schedule.dao.DepartmentDao;
import ru.bmstu.schedule.dao.FacultyDao;
import ru.bmstu.schedule.dao.LecturerDao;
import ru.bmstu.schedule.entity.Department;
import ru.bmstu.schedule.entity.Faculty;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DaoTest {
    private static SessionFactory sessionFactory;

    @BeforeAll
    public static void beforeTestsStarted() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @AfterAll
    public static void afterTestsFinished() {
        // Remove created entities:

        FacultyDao facultyDao = new FacultyDao(sessionFactory);
        Optional<Faculty> createdFaculty = facultyDao.findByCipher("М");
        createdFaculty.ifPresent(facultyDao::delete);


        sessionFactory.close();
    }

    @Test
    public void testDepartmentDao() {
        DepartmentDao dao = new DepartmentDao(sessionFactory);

        System.out.println(dao.findByCipher("ИУ9"));
    }

    @Test
    public void testFacultyDao() {
        FacultyDao facultyDao = new FacultyDao(sessionFactory);

        facultyDao.findByCipher("М").ifPresent(facultyDao::delete);

        Faculty factMT = facultyDao.findByKey(421);

        assertEquals("МТ", factMT.getCipher());
        assertEquals("Машиностроительные технологии", factMT.getTitle());

        Faculty fakeFact = new Faculty();

        fakeFact.setCipher("М");
        fakeFact.setTitle("Медицинский");

        int fakeFactPk = facultyDao.create(fakeFact);

        fakeFact.setTitle("Медицинский факультет");

        facultyDao.update(fakeFact);

        assertEquals("Медицинский факультет", facultyDao.findByKey(fakeFactPk).getTitle());

        System.out.println("List of all faculties: ");

        for(Faculty fact : facultyDao.findAll()) {
            System.out.println(fact);
        }
    }

    @Test
    public void testFindLecturerByInitials() {
        String initials = "Голубков А. Ю.";
        LecturerDao lecturerDao = new LecturerDao(sessionFactory);

        System.out.println("Found lecturer: " + lecturerDao.findFirstByInitials(initials));

    }

}
