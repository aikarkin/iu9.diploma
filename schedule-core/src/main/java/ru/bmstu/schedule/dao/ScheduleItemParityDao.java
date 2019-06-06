package ru.bmstu.schedule.dao;

import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.ScheduleItemParity;

public class ItemParityDao extends HibernateDao<Integer, ScheduleItemParity> {

    public ItemParityDao(SessionFactory factory) {
        super(factory);
    }

}
