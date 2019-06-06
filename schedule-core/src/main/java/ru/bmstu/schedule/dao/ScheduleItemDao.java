package ru.bmstu.schedule.dao;

import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.ScheduleItem;

public class ScheduleItemDao extends HibernateDao<Integer, ScheduleItem> {

    public ScheduleItemDao(SessionFactory factory) {
        super(factory);
    }

}
