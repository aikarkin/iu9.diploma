package ru.bmstu.schedule.dao;

import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.ClassType;

import java.util.Optional;

public class ClassTypeDao extends HibernateDao<Integer, ClassType> {
    public ClassTypeDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<ClassType> findByTypeName(String typeName) {
        return findUniqueByProperty("name", typeName);
    }
}
