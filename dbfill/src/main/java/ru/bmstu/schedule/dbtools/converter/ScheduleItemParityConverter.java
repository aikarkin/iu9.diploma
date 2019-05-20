package ru.bmstu.schedule.dbtools.converter;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import ru.bmstu.schedule.dao.*;
import ru.bmstu.schedule.entity.*;
import ru.bmstu.schedule.html.node.ScheduleItemParityNode;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

public class ScheduleItemParityConverter implements NodeToEntityConverter<ScheduleItemParityNode, ru.bmstu.schedule.entity.ScheduleItemParity> {
    private SessionFactory sessionFactory;
    private LecturerDao lecDao;
    private SubjectDao subjDao;
    private ClassTypeDao ctDao;
    private ClassroomDao crDao;
    private ItemParityDao parityDao;


    public ScheduleItemParityConverter(SessionFactory sessionFactory) {
        lecDao = new LecturerDao(sessionFactory);
        subjDao = new SubjectDao(sessionFactory);
        ctDao = new ClassTypeDao(sessionFactory);
        crDao = new ClassroomDao(sessionFactory);
        parityDao = new ItemParityDao(sessionFactory);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public ru.bmstu.schedule.entity.ScheduleItemParity convert(ScheduleItemParityNode node) {
        ru.bmstu.schedule.entity.ScheduleItemParity itemParity = new ru.bmstu.schedule.entity.ScheduleItemParity();
        Optional<Subject> subjOpt = subjDao.findByName(node.getSubject());
        Optional<Classroom> crOpt = crDao.findByRoomNumber(node.getClassroom());
        Optional<ClassType> ctOpt = ctDao.findByTypeName(node.getClassType());
//        System.out.println("[debug] Item: " + node.getParent());

        if(StringUtils.isNotEmpty(node.getLecturer())) {
            try {
                List<Lecturer> foundLecs = lecDao.findByInitials(node.getLecturer());
                if(foundLecs.size() > 0) {
//                    System.out.println("[debug] Add lecturer: " + foundLecs.get(0));
                    itemParity.addLecturer(foundLecs.get(0));
                }
            } catch (InvalidParameterException e) {
                e.printStackTrace();
            }
        }

        crOpt.ifPresent(itemParity::setClassroom);
        ctOpt.ifPresent(itemParity::setClassType);
        subjOpt.ifPresent(itemParity::setSubject);
        itemParity.setDayParity(node.getDayParity().toString());


        return itemParity;
    }
}
