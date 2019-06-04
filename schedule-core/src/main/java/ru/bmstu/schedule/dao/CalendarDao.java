package ru.bmstu.schedule.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.Calendar;

import java.util.List;
import java.util.Optional;

public class CalendarDao extends HibernateDao<Integer, Calendar> {

    public CalendarDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<Calendar> findByStartYearAndDepartmentCodeAndSpecCode(int year, String deptCipher, String specCode) {
        List<Calendar> calendarList = composeInTransaction(session -> {
            Query calendarQuery = session.createQuery("SELECT c FROM Calendar c " +
                    "LEFT OUTER JOIN c.departmentSpecialization ds " +
                    "LEFT OUTER JOIN c.departmentSpecialization.department dept " +
                    "LEFT OUTER JOIN c.departmentSpecialization.department.faculty fact " +
                    "LEFT OUTER JOIN c.departmentSpecialization.specialization spec " +
                    "LEFT OUTER JOIN c.departmentSpecialization.specialization.speciality st " +
                    "WHERE c.startYear = :startYear " +
                    "AND CONCAT(dept.faculty.cipher, CAST(dept.number AS string)) = :department " +
                    "AND CONCAT(st.code, '_', CAST(spec.numberInSpeciality AS string)) = :specialization");

            calendarQuery.setParameter("startYear", year);
            calendarQuery.setParameter("department", deptCipher);
            calendarQuery.setParameter("specialization", specCode);

            return (List<Calendar>) calendarQuery.list();
        });

        if (calendarList.size() == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(calendarList.get(0));
    }

}
