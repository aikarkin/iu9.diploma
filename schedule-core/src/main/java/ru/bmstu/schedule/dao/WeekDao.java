package ru.bmstu.schedule.dao;

import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.DayOfWeek;

import java.util.Optional;

public class WeekDao extends HibernateDao<Integer, DayOfWeek> {

    public WeekDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<DayOfWeek> findByName(String name) {
        return findUniqueByProperty("name", name);
    }

}
