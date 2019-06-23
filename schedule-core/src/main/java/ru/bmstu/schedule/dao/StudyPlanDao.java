package ru.bmstu.schedule.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.StudyPlan;

import java.util.List;
import java.util.Optional;

public class StudyPlanDao extends HibernateDao<Integer, StudyPlan> {

    public StudyPlanDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<StudyPlan> findByStartYearAndDepartmentCipherAndSpecCode(int year, String deptCipher, String specCode) {
        List<StudyPlan> studyPlanList = composeInTransaction(session -> {
            Query calendarQuery = session.createQuery("SELECT sp FROM StudyPlan sp " +
                    "LEFT OUTER JOIN sp.departmentSpecialization ds " +
                    "LEFT OUTER JOIN sp.departmentSpecialization.department dept " +
                    "LEFT OUTER JOIN sp.departmentSpecialization.department.faculty fact " +
                    "LEFT OUTER JOIN sp.departmentSpecialization.specialization spec " +
                    "LEFT OUTER JOIN sp.departmentSpecialization.specialization.speciality st " +
                    "WHERE sp.startYear = :startYear " +
                    "AND CONCAT(dept.faculty.cipher, CAST(dept.number AS string)) = :department " +
                    "AND CONCAT(st.code, '_', CAST(spec.numberInSpeciality AS string)) = :specialization");

            calendarQuery.setParameter("startYear", year);
            calendarQuery.setParameter("department", deptCipher);
            calendarQuery.setParameter("specialization", specCode);

            return (List<StudyPlan>) calendarQuery.list();
        });

        if (studyPlanList.size() == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(studyPlanList.get(0));
    }

}
