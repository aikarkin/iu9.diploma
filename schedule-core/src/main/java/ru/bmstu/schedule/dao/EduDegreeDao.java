package ru.bmstu.schedule.dao;

import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.EduDegree;

import java.util.Optional;

public class EduDegreeDao extends HibernateDao<Integer, EduDegree> {

    public EduDegreeDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<EduDegree> findByName(String degreeName) {
        return findUniqueByProperty("name", degreeName);
    }

}
