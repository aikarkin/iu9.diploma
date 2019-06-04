package ru.bmstu.schedule.dao;

import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.Faculty;

import java.util.Optional;

public class FacultyDao extends HibernateDao<Integer, Faculty> {

    public FacultyDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<Faculty> findByCipher(String facultyCipher) {
        return findUniqueByProperty("cipher", facultyCipher);
    }

}
