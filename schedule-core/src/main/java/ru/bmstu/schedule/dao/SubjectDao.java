package ru.bmstu.schedule.dao;

import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.Subject;

import java.util.Optional;

public class SubjectDao extends HibernateDao<Integer, Subject> {

    public SubjectDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<Subject> findByName(String name) {
        return findUniqueByProperty("name", name);
    }

}
