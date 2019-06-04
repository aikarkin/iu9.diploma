package ru.bmstu.schedule.dao;

import org.junit.jupiter.api.Test;
import ru.bmstu.schedule.entity.DepartmentSubject;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DepartmentSubjectDaoTest extends DatabaseAccessTest {

    @Test
    void findByDepartmentAndSubject() {
        DepartmentSubjectDao departmentSubjectDao = new DepartmentSubjectDao(getSessionFactory());
        Optional<DepartmentSubject> deptSubj = departmentSubjectDao.findByDepartmentCipherAndSubjectName("ИУ9", "Основы информатики");
        assertTrue(deptSubj.isPresent());
        assertEquals(deptSubj.get().getDepartment().getNumber(), 9);
        assertEquals(deptSubj.get().getSubject().getName(), "Основы информатики");
    }

}