package ru.bmstu.schedule.dao;

import org.junit.jupiter.api.Test;
import ru.bmstu.schedule.entity.Faculty;
import ru.bmstu.schedule.entity.StudyGroup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;


public class DaoTest extends DatabaseAccessTest {

    @Test
    public void testDepartmentDao() {
        DepartmentDao dao = new DepartmentDao(getSessionFactory());

        System.out.println(dao.findByCipher("ИУ9"));
    }

    @Test
    public void testDeleteAll() {
        ScheduleItemParityDao scheduleItemParityDao = new ScheduleItemParityDao(getSessionFactory());
        scheduleItemParityDao.deleteAll();
        assertEquals(0, scheduleItemParityDao.findAll().size());
    }

    @Test
    public void testFacultyDao() {
        FacultyDao facultyDao = new FacultyDao(getSessionFactory());

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

        for (Faculty fact : facultyDao.findAll()) {
            System.out.println(fact);
        }
    }

    @Test
    public void testFindLecturerByInitials() {
        String initials = "Голубков А. Ю.";
        LecturerDao lecturerDao = new LecturerDao(getSessionFactory());

        System.out.println("Found lecturer: " + lecturerDao.findFirstByInitials(initials));

    }

    @Test
    public void testCipherRegex() {
        Pattern ptr = Pattern.compile("(\\p{Lu}+)(\\d+)?-(\\d+?)(\\d)(\\p{Lu})?");
        String cipher1 = "ИУ9-72";
        String cipher2 = "ЮР-92";
        String cipher3 = "ЮР-11М";

        Matcher matcher = ptr.matcher(cipher1);
        assertTrue(matcher.matches() && matcher.groupCount() == 5);
        assertEquals("ИУ", matcher.group(1));
        assertEquals("9", matcher.group(2));
        assertEquals("7", matcher.group(3));
        assertEquals("2", matcher.group(4));
        assertNull(matcher.group(5));

        matcher = ptr.matcher(cipher2);
        assertTrue(matcher.matches() && matcher.groupCount() == 5);
        assertEquals("ЮР", matcher.group(1));
        assertNull(matcher.group(2));
        assertEquals("9", matcher.group(3));
        assertEquals("2", matcher.group(4));
        assertNull(matcher.group(5));

        matcher = ptr.matcher(cipher3);
        assertTrue(matcher.matches() && matcher.groupCount() == 5);
        assertEquals("ЮР", matcher.group(1));
        assertNull(matcher.group(2));
        assertEquals("1", matcher.group(3));
        assertEquals("1", matcher.group(4));
        assertEquals("М", matcher.group(5));
    }

    @Test
    public void testGroupDao() {
        StudyGroupDao dao = new StudyGroupDao(getSessionFactory());

        StudyGroup group = dao.findByKey(3234);
//        Optional<StudyGroup> groupOpt = dao.findByCipher("ИУ9-72Б");
////        assertTrue(groupOpt.isPresent());
////
////        assertEquals(2, groupOpt.get().getNumber());
////        assertEquals(7, groupOpt.get().getTerm().getNumber());
////        assertEquals(9, groupOpt.get().getStudyFlow().getDepartment().getNumber());
////        assertEquals("ИУ", groupOpt.get().getStudyFlow().getDepartment().getFaculty().getCipher());
////        assertEquals("бакалавариат", groupOpt.get().getStudyFlow().getSpecialization().getEduDegree().getName().trim());
    }

}
