package ru.bmstu.schedule.dao;

import org.junit.jupiter.api.Test;
import ru.bmstu.schedule.entity.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LecturerSubjectDaoTest extends DatabaseAccessTest {

    @Test
    void findByLecturerAndDepartmentSubjectAndClassType() {
        LecturerSubjectDao lecSubjDao = new LecturerSubjectDao(getSessionFactory());
        LecturerDao lecDao = new LecturerDao(getSessionFactory());
        DepartmentSubjectDao deptSubjDao = new DepartmentSubjectDao(getSessionFactory());
        ClassTypeDao ctDao = new ClassTypeDao(getSessionFactory());

        List<Lecturer> lecturers = lecDao.findByInitials("Дубанов А. В.");
        Optional<DepartmentSubject> deptSubj = deptSubjDao.findByDepartmentCipherAndSubjectName("ИУ9","Основы информатики");
        Optional<ClassType> ctOpt = ctDao.findByName("семинар");

        assertEquals(1, lecturers.size());
        assertTrue(deptSubj.isPresent());
        assertTrue(ctOpt.isPresent());


        Optional<LecturerSubject> lecSubjOpt = lecSubjDao.findByLecturerAndDepartmentSubjectAndClassType(lecturers.get(0), deptSubj.get(), ctOpt.get());
        assertTrue(lecSubjOpt.isPresent());
        assertEquals(lecSubjOpt.get().getClassType(), ctOpt.get());
        assertEquals(lecSubjOpt.get().getDepartmentSubject().getSubject(), deptSubj.get().getSubject());
        assertEquals(lecSubjOpt.get().getLecturer(), lecturers.get(0));
    }

}