package ru.bmstu.schedule.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.ClassType;

import java.util.Optional;

public class ClassTypeDao extends HibernateDao<Integer, ClassType> {

    public ClassTypeDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<ClassType> findByName(String typeName) {
        return findUniqueByProperty("name", typeName);
    }

    public Optional<ClassType> findByShortName(String shortName) {
        if (!shortName.matches("\\p{L}{3}")) {
            return Optional.empty();
        }

        return Optional.ofNullable(composeInTransaction(session -> {
            Query query = session.createQuery("SELECT ct FROM ClassType ct WHERE ct.name LIKE concat(:shortName, '%')");
            query.setParameter("shortName", shortName);
            return (ClassType) query.uniqueResult();
        }));
    }

}
