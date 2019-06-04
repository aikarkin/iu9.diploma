package ru.bmstu.schedule.dao;

import org.junit.jupiter.api.Test;
import ru.bmstu.schedule.entity.ClassType;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ClassTypeDaoTest extends DatabaseAccessTest {

    @Test
    void testFindByShortName() {
        ClassTypeDao ctDao = new ClassTypeDao(getSessionFactory());
        Optional<ClassType> validShortName = ctDao.findByShortName("сем");
        Optional<ClassType> invalidShortName = ctDao.findByShortName("");
        assertTrue(validShortName.isPresent());
        assertEquals(validShortName.get().getName(), "семинар");

        assertFalse(invalidShortName.isPresent());
    }

}