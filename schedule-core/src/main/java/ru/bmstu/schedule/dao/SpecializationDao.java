package ru.bmstu.schedule.dao;

import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.Specialization;

import java.util.Optional;

public class SpecializationDao extends HibernateDao<Integer, Specialization> {
    public SpecializationDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<Specialization> findByCode(String specCode) {
        return findUniqueByProperty("code", specCode);
    }
}
