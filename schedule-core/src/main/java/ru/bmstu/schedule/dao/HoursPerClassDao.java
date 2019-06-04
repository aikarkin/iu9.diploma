package ru.bmstu.schedule.dao;

import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.HoursPerClass;

public class HoursPerClassDao extends HibernateDao<Integer, HoursPerClass> {

    public HoursPerClassDao(SessionFactory factory) {
        super(factory);
    }

}
