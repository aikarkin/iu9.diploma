package ru.bmstu.schedule.dao;

import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.CalendarItem;

public class CalendarItemDao extends HibernateDao<Integer, CalendarItem> {
    public CalendarItemDao(SessionFactory factory) {
        super(factory);
    }
}
