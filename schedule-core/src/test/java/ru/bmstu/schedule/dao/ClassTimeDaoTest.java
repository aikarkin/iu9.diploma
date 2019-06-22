package ru.bmstu.schedule.dao;

import org.hibernate.HibernateException;
import org.junit.jupiter.api.*;
import ru.bmstu.schedule.entity.ClassTime;

import java.sql.Time;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClassTimeDaoTest extends DatabaseAccessTest {

    private static ClassTimeDao dao;
    private static final Time STARTS_AT = Time.valueOf("21:00:00");
    private static final Time ENDS_AT = Time.valueOf("22:00:00");
    private static final int ORDER = 8;
    

    @BeforeAll
    static void setUp() {
        dao = new ClassTimeDao(getSessionFactory());
    }

    @Test
    @Order(1)
    void testCreate() {
        int curId;
        // correct CT:
        curId = dao.create(new ClassTime(ORDER, STARTS_AT, ENDS_AT));
        Optional<ClassTime> ctOpt = dao.findByKey(curId);

        assertTrue(ctOpt.isPresent());

        assertEquals(ctOpt.get().getStartsAt(), STARTS_AT);
        assertEquals(ctOpt.get().getEndsAt(), ENDS_AT);
        assertEquals(ctOpt.get().getNoOfClass(), ORDER);

        // order of class cannot be negative:
        assertThrows(HibernateException.class, () -> dao.create(new ClassTime(-1, STARTS_AT, ENDS_AT)));
        // end time < start time
        assertThrows(HibernateException.class, () -> dao.create(new ClassTime(1, ENDS_AT, STARTS_AT)));
        // start time == end time
        assertThrows(HibernateException.class, () -> dao.create(new ClassTime(1, ENDS_AT, ENDS_AT)));
        // create second ClassTime with same time period
        assertThrows(HibernateException.class, () -> dao.create(new ClassTime(10, STARTS_AT, ENDS_AT)));
    }

    @Test
    @Order(2)
    void testFindByDuration() {
        Optional<ClassTime> ctOpt = dao.findByStartAndEndTime(STARTS_AT, ENDS_AT);
        assertTrue(ctOpt.isPresent());

        assertEquals(ctOpt.get().getStartsAt(), STARTS_AT);
        assertEquals(ctOpt.get().getEndsAt(), ENDS_AT);
        assertEquals(ctOpt.get().getNoOfClass(), ORDER);
    }

    @Test
    @Order(3)
    void testUpdate() {
        Time startsAt = Time.valueOf("22:00:00");
        Time endsAt = Time.valueOf("23:00:00");
        int order = 9;

        Optional<ClassTime> ctOpt = dao.findByOrderNumber(ORDER);
        assertTrue(ctOpt.isPresent());

        ClassTime ct = ctOpt.get();

        // valid update:
        ct.setStartsAt(startsAt);
        ct.setEndsAt(endsAt);
        ct.setNoOfClass(order);
        dao.update(ct);

        ctOpt = dao.findByOrderNumber(order);
        assertTrue(ctOpt.isPresent());

        assertEquals(ctOpt.get().getStartsAt(), startsAt);
        assertEquals(ctOpt.get().getEndsAt(), endsAt);
        assertEquals(ctOpt.get().getNoOfClass(), order);

        // invalid updates:
        ClassTime invalidCT = ctOpt.get();
        invalidCT.setNoOfClass(-1);
        assertThrows(HibernateException.class, () -> dao.update(invalidCT));

        invalidCT.setStartsAt(endsAt);
        assertThrows(HibernateException.class, () -> dao.update(invalidCT));
    }

    @Test
    @Order(4)
    void testDelete() {
        Optional<ClassTime> ctOpt = dao.findByOrderNumber(9);
        assertTrue(ctOpt.isPresent());
        dao.delete(ctOpt.get());
        assertFalse(dao.findByOrderNumber(9).isPresent());
    }

}