package ru.bmstu.schedule.dao;

import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.Term;

public class TermDao extends HibernateDao<Integer, Term> {
    public TermDao(SessionFactory factory) {
        super(factory);
    }
}
