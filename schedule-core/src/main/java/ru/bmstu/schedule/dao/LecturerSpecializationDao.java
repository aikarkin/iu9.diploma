package ru.bmstu.schedule.dao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import ru.bmstu.schedule.entity.Lecturer;
import ru.bmstu.schedule.entity.Specialization;
import ru.bmstu.schedule.entity.SpecializationOfLecturer;

import java.util.Optional;

public class LecturerSpecializationDao extends HibernateDao<Integer, SpecializationOfLecturer> {

    public LecturerSpecializationDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<SpecializationOfLecturer> findBySpecAndLec(Specialization spec, Lecturer lec) {
        return composeInTransaction(session -> {
            Criteria criteria = createEntityCriteria();
            criteria.add(Restrictions.eq("specialization", spec));
            criteria.add(Restrictions.eq("lecturer", lec));

            try {
                return Optional.ofNullable((SpecializationOfLecturer) criteria.uniqueResult());
            } catch (HibernateException e) {
                return Optional.empty();
            }
        });
    }

}
