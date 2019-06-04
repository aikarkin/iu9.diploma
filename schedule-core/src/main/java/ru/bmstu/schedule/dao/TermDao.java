package ru.bmstu.schedule.dao;

import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.Term;

import java.util.Optional;

public class TermDao extends HibernateDao<Integer, Term> {

    public TermDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<Term> findByNumber(int number) {
        return findUniqueByProperty("number", number);
    }

}
