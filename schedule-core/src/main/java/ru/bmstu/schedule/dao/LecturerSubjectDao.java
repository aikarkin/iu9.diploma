package ru.bmstu.schedule.dao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import ru.bmstu.schedule.entity.Lecturer;
import ru.bmstu.schedule.entity.Subject;
import ru.bmstu.schedule.entity.SubjectOfLecturer;

import java.util.Optional;

public class LecturerSubjectDao extends HibernateDao<Integer, SubjectOfLecturer> {

    public LecturerSubjectDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<SubjectOfLecturer> findBySubjectAndLecturer(Subject subject, Lecturer lec) {
        return composeInTransaction(session -> {
            Criteria criteria = createEntityCriteria();
            criteria.add(Restrictions.eq("subject", subject));
            criteria.add(Restrictions.eq("lecturer", lec));

            try {
                return Optional.ofNullable((SubjectOfLecturer) criteria.uniqueResult());
            } catch (HibernateException e) {
                return Optional.empty();
            }
        });
    }

}
