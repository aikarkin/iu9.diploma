package ru.bmstu.schedule.dao;

import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.Classroom;

import java.util.Optional;

public class ClassroomDao extends HibernateDao<Integer, Classroom> {
    public ClassroomDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<Classroom> findByRoomNumber(String roomNumber) {
        return findUniqueByProperty("roomNumber", roomNumber);
    }
}
