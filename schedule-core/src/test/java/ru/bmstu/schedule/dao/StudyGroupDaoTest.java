package ru.bmstu.schedule.dao;

import org.junit.jupiter.api.Test;
import ru.bmstu.schedule.entity.StudyGroup;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StudyGroupDaoTest extends DatabaseAccessTest {

    @Test
    void findByCipher() {
        StudyGroupDao groupDao = new StudyGroupDao(getSessionFactory());
        Optional<StudyGroup> grOpt = groupDao.findByCipher("ИУ9-21");
        assertTrue(grOpt.isPresent());
    }

}