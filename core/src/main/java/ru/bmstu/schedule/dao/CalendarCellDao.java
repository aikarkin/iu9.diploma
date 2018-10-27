package ru.bmstu.schedule.dao;

import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.CalendarItemCell;

public class CalendarCellDao extends HibernateDao<Integer, CalendarItemCell> {
    public CalendarCellDao(SessionFactory factory) {
        super(factory);
    }
}
