package ru.bmstu.schedule.dao;

import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.StudyGroup;

public class StudyGroupDao extends HibernateDao<Integer, StudyGroup> {
    public StudyGroupDao(SessionFactory factory) {
        super(factory);
    }

    @Override
    public Integer create(StudyGroup studyGroup) {
        Integer id = super.create(studyGroup);
        studyGroup.getScheduleDays()
                .stream()
                .flatMap(sd -> sd.getScheduleItems().stream())
                .flatMap(si -> si.getScheduleItemParities().stream())
                .flatMap(ip -> ip.getLecturers().stream())
                .forEach(lecturer -> {
                    composeInTransaction(session ->  {
                        session.save(lecturer);
                        return null;
                    });
                });
        return id;
    }

}
