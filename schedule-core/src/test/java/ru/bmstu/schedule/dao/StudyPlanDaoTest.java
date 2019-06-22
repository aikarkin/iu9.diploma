package ru.bmstu.schedule.dao;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.bmstu.schedule.entity.StudyPlan;
import ru.bmstu.schedule.entity.Department;
import ru.bmstu.schedule.entity.DepartmentSpecialization;
import ru.bmstu.schedule.entity.Specialization;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StudyPlanDaoTest extends DatabaseAccessTest {

    private static final String VALID_SPEC_CODE = "01.03.02_1";
    private static final String INVALID_SPEC_CODE = "01.03.02_156";
    private static final String VALID_DEPT_CIPHER = "ИУ9";
    private static final int VALID_ENROLLMENT_YEAR = 2018;

    private static StudyPlan validStudyPlan;
    private static DepartmentSpecialization VALID_DEPT_SPEC;

    @SuppressWarnings("Duplicates")
    @BeforeAll
    static void addCalendars() {
        SessionFactory factory = getSessionFactory();
        SpecializationDao specializationDao = new SpecializationDao(factory);
        Optional<Specialization> specOpt = specializationDao.findByCode(VALID_SPEC_CODE);
        if (!specOpt.isPresent()) {
            throw new IllegalStateException("Invalid database state: No specialization found with code: " + VALID_SPEC_CODE);
        }

        DepartmentDao deptDao = new DepartmentDao(factory);
        Optional<Department> deptOpt = deptDao.findByCipher(VALID_DEPT_CIPHER);
        if (!deptOpt.isPresent()) {
            throw new IllegalStateException("Invalid database state: No department found with cipher: " + VALID_DEPT_CIPHER);
        }

        DepartmentSpecializationDao deptSpecDao = new DepartmentSpecializationDao(factory);
        VALID_DEPT_SPEC = new DepartmentSpecialization();
        VALID_DEPT_SPEC.setDepartment(deptOpt.get());
        VALID_DEPT_SPEC.setSpecialization(specOpt.get());
        Integer deptSpecId = deptSpecDao.create(VALID_DEPT_SPEC);
        VALID_DEPT_SPEC.setId(deptSpecId);

        StudyPlanDao studyPlanDao = new StudyPlanDao(factory);
        validStudyPlan = new StudyPlan();
        validStudyPlan.setDepartmentSpecialization(VALID_DEPT_SPEC);
        validStudyPlan.setStartYear(VALID_ENROLLMENT_YEAR);

        Integer calendarId = studyPlanDao.create(validStudyPlan);
        validStudyPlan.setId(calendarId);
    }

    @Test
    void findByStartYearAndDepartmentCodeAndSpecCode() {
        StudyPlanDao studyPlanDao = new StudyPlanDao(getSessionFactory());
        Optional<StudyPlan> validCalendarOpt = studyPlanDao.findByStartYearAndDepartmentCodeAndSpecCode(
                VALID_ENROLLMENT_YEAR,
                VALID_DEPT_CIPHER,
                VALID_SPEC_CODE
        );
        assertTrue(validCalendarOpt.isPresent());
        assertEquals(validCalendarOpt.get().getStartYear(), VALID_ENROLLMENT_YEAR);
        assertEquals(validCalendarOpt.get().getDepartmentSpecialization().getDepartment().getNumber(), 9);
        assertEquals(validCalendarOpt.get().getDepartmentSpecialization().getSpecialization().getNumberInSpeciality(), 1);

        Optional<StudyPlan> invalidCalendarOpt = studyPlanDao.findByStartYearAndDepartmentCodeAndSpecCode(
                VALID_ENROLLMENT_YEAR,
                VALID_DEPT_CIPHER,
                INVALID_SPEC_CODE
        );

        assertFalse(invalidCalendarOpt.isPresent());
    }

    @AfterAll
    static void removeCalendars() {
        SessionFactory factory = getSessionFactory();
        DepartmentSpecializationDao deptSpecDao = new DepartmentSpecializationDao(factory);
        StudyPlanDao studyPlanDao = new StudyPlanDao(factory);
        studyPlanDao.delete(validStudyPlan);
        deptSpecDao.delete(VALID_DEPT_SPEC);
    }

}