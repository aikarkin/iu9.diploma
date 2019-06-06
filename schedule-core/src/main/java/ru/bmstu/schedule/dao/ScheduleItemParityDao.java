package ru.bmstu.schedule.dao;

import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.ScheduleItemParity;

public class ScheduleItemParityDao extends HibernateDao<Integer, ScheduleItemParity> {

    public ScheduleItemParityDao(SessionFactory factory) {
        super(factory);
    }

}
