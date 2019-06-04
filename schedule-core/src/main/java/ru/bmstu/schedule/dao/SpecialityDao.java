package ru.bmstu.schedule.dao;

import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.Speciality;

import java.util.Optional;

public class SpecialityDao extends HibernateDao<Integer, Speciality> {

    public SpecialityDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<Speciality> findByCode(String specCode) {
        return findUniqueByProperty("code", specCode);
    }

}
