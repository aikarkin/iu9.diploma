package ru.bmstu.schedule.dao;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import ru.bmstu.schedule.entity.ClassTime;

import java.util.List;

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
}
