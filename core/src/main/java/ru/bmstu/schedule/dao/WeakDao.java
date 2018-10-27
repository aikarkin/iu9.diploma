package ru.bmstu.schedule.dao;

import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.DayOfWeak;

import java.util.Optional;

public class WeakDao extends HibernateDao<Integer, DayOfWeak> {
    public WeakDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<DayOfWeak> findByShortName(String shortName) {
        return findUniqueByProperty("shortName", shortName);
    }
}
