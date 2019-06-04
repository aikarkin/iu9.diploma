package ru.bmstu.schedule.dao;

import org.junit.jupiter.api.Test;
import ru.bmstu.schedule.entity.Specialization;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SpecializationDaoTest extends DatabaseAccessTest {

    @Test
    void testFindByCode() {
        SpecializationDao specializationDao = new SpecializationDao(getSessionFactory());
        Optional<Specialization> validSpecOpt = specializationDao.findByCode("25.06.01_4");
        assertTrue(validSpecOpt.isPresent());
        assertEquals(validSpecOpt.get().getSpeciality().getCode(), "25.06.01");
        assertEquals(validSpecOpt.get().getNumberInSpeciality(), 4);

        Optional<Specialization> invalidSpecOpt = specializationDao.findByCode("25.06.51_4234");
        assertFalse(invalidSpecOpt.isPresent());
    }

}