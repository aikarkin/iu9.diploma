package ru.bmstu.schedule.dao;

import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.ScheduleDay;

public class ScheduleDayDao extends HibernateDao<Integer, ScheduleDay> {

    public ScheduleDayDao(SessionFactory factory) {
        super(factory);
    }

}
