package ru.bmstu.schedule.dao;

import org.junit.jupiter.api.Test;
import ru.bmstu.schedule.entity.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TutorSubjectDaoTest extends DatabaseAccessTest {

    @Test
    void findByLecturerAndDepartmentSubjectAndClassType() {
        TutorSubjectDao lecSubjDao = new TutorSubjectDao(getSessionFactory());
        TutorDao lecDao = new TutorDao(getSessionFactory());
        DepartmentSubjectDao deptSubjDao = new DepartmentSubjectDao(getSessionFactory());
        ClassTypeDao ctDao = new ClassTypeDao(getSessionFactory());

        List<Tutor> tutors = lecDao.findByInitials("Дубанов А. В.");
        Optional<DepartmentSubject> deptSubj = deptSubjDao.findByDepartmentCipherAndSubjectName("ИУ9","Основы информатики");
        Optional<ClassType> ctOpt = ctDao.findByName("семинар");

        assertEquals(1, tutors.size());
        assertTrue(deptSubj.isPresent());
        assertTrue(ctOpt.isPresent());


        Optional<TutorSubject> lecSubjOpt = lecSubjDao.findByTutorAndDepartmentSubjectAndClassType(tutors.get(0), deptSubj.get(), ctOpt.get());
        assertTrue(lecSubjOpt.isPresent());
        assertEquals(lecSubjOpt.get().getClassType(), ctOpt.get());
        assertEquals(lecSubjOpt.get().getDepartmentSubject().getSubject(), deptSubj.get().getSubject());
        assertEquals(lecSubjOpt.get().getTutor(), tutors.get(0));
    }

}