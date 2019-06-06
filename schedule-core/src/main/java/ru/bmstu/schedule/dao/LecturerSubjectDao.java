package ru.bmstu.schedule.dao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import ru.bmstu.schedule.entity.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class LecturerSubjectDao extends HibernateDao<Integer, LecturerSubject> {

    public LecturerSubjectDao(SessionFactory factory) {
        super(factory);
    }

    @SuppressWarnings("unchecked")
    public List<LecturerSubject> findByLecturerAndDepartmentSubject(Lecturer lecturer, DepartmentSubject departmentSubject) {
        if (lecturer == null || departmentSubject == null) {
            return Collections.emptyList();
        }

        return composeInTransaction(session -> {
            Criteria criteria = session.createCriteria(LecturerSubject.class);
            criteria.add(Restrictions.eq("lecturer", lecturer));
            criteria.add(Restrictions.eq("departmentSubject", departmentSubject));
            return (List<LecturerSubject>) criteria.list();
        });
    }

    public Optional<LecturerSubject> findByLecturerAndDepartmentSubjectAndClassType(Lecturer lecturer, DepartmentSubject subject, ClassType classType) {
        return Optional.ofNullable(composeInTransaction(session -> {
            try {
                Criteria criteria = session.createCriteria(LecturerSubject.class);
                criteria.add(Restrictions.eq("lecturer", lecturer));
                criteria.add(Restrictions.eq("departmentSubject", subject));
                criteria.add(Restrictions.eq("classType", classType));

                return (LecturerSubject) criteria.uniqueResult();
            } catch (HibernateException e) {
                return null;
            }
        }));
    }

}
