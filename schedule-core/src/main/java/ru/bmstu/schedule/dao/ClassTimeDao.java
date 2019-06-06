package ru.bmstu.schedule.dao;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import ru.bmstu.schedule.entity.ClassTime;

import java.sql.Time;
import java.util.List;
import java.util.Optional;

public class ClassTimeDao extends HibernateDao<Integer, ClassTime> {

    public ClassTimeDao(SessionFactory factory) {
        super(factory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ClassTime> findAll() {
        return composeInTransaction(session -> {
            Criteria criteria = createEntityCriteria();
            criteria.addOrder(Order.asc("startsAt"));
            criteria.addOrder(Order.asc("endsAt"));
            return (List<ClassTime>) createEntityCriteria().list();
        });
    }

    public Optional<ClassTime> findByStartAndEndTime(Time startAt, Time endsAt) {
        return composeInTransaction(session -> {
            Criteria criteria = createEntityCriteria();
            criteria.add(Restrictions.eq("startsAt", startAt));
            criteria.add(Restrictions.eq("endsAt", endsAt));
            List found = criteria.list();

            return Optional.ofNullable(found.size() > 0 ? (ClassTime) found.get(0) : null);
        });
    }

    public Optional<ClassTime> findByOrderNumber(int order) {
        return findUniqueByProperty("noOfClass", order);
    }

}
