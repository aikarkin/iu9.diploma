package ru.bmstu.schedule.dao;

import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.StudyFlow;

public class StudyFlowDao extends HibernateDao<Integer, StudyFlow> {
    public StudyFlowDao(SessionFactory factory) {
        super(factory);
    }
}
