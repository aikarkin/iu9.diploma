package ru.bmstu.schedule.dao;

import org.junit.jupiter.api.Test;
import ru.bmstu.schedule.entity.Department;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DepartmentDaoTest extends DatabaseAccessTest {

    @Test
    void testFindByCipher() {
        DepartmentDao deptDao = new DepartmentDao(getSessionFactory());
        Optional<Department> dept1 = deptDao.findByCipher("ФВ");
        Optional<Department> dept2 = deptDao.findByCipher("ЮР");
        assertTrue(dept1.isPresent());
        assertEquals(dept1.get().getTitle(), "Физическое воспитание");

        assertTrue(dept2.isPresent());
        assertEquals(dept2.get().getTitle(), "Юриспруденция, интеллектуальная собственность и судебная экспертиза");
    }

}