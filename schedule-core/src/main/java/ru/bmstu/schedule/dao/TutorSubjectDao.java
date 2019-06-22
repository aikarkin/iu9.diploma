package ru.bmstu.schedule.dao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import ru.bmstu.schedule.entity.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TutorSubjectDao extends HibernateDao<Integer, TutorSubject> {

    public TutorSubjectDao(SessionFactory factory) {
        super(factory);
    }

    @SuppressWarnings("unchecked")
    public List<TutorSubject> findByLecturerAndDepartmentSubject(Tutor tutor, DepartmentSubject departmentSubject) {
        if (tutor == null || departmentSubject == null) {
            return Collections.emptyList();
        }

        return composeInTransaction(session -> {
            Criteria criteria = session.createCriteria(TutorSubject.class);
            criteria.add(Restrictions.eq("lecturer", tutor));
            criteria.add(Restrictions.eq("departmentSubject", departmentSubject));
            return (List<TutorSubject>) criteria.list();
        });
    }

    public Optional<TutorSubject> findByLecturerAndDepartmentSubjectAndClassType(Tutor tutor, DepartmentSubject subject, ClassType classType) {
        return Optional.ofNullable(composeInTransaction(session -> {
            try {
                Criteria criteria = session.createCriteria(TutorSubject.class);
                criteria.add(Restrictions.eq("lecturer", tutor));
                criteria.add(Restrictions.eq("departmentSubject", subject));
                criteria.add(Restrictions.eq("classType", classType));

                return (TutorSubject) criteria.uniqueResult();
            } catch (HibernateException e) {
                return null;
            }
        }));
    }

}
