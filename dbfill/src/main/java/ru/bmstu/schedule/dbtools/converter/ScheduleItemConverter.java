//package ru.bmstu.schedule.dbtools.converter;
//
//import org.apache.commons.lang3.StringUtils;
//import org.hibernate.SessionFactory;
//import ru.bmstu.schedule.dao.ClassTimeDao;
//import ru.bmstu.schedule.entity.ClassTime;
//import ru.bmstu.schedule.entity.ScheduleItem;
//import ru.bmstu.schedule.entity.ScheduleItemParity;
//import ru.bmstu.schedule.html.node.ScheduleItemNode;
//
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//public class ScheduleItemConverter implements NodeToEntityConverter<ScheduleItemNode, ScheduleItem> {
//    private SessionFactory sessionFactory;
//    private ClassTimeDao ctDao;
//
//    public ScheduleItemConverter(SessionFactory factory) {
//        ctDao = new ClassTimeDao(factory);
//        this.sessionFactory = factory;
//    }
//
//    @Override
//    public ScheduleItem convert(ScheduleItemNode node) {
//        ScheduleItem item = new ScheduleItem();
//        Optional<ClassTime> ct = ctDao.findByStartAndEndTime(node.getStartsAt(), node.getEndsAt());
//        ct.ifPresent(item::setClassTime);
//
//        ScheduleItemParityConverter childConverter = new ScheduleItemParityConverter(sessionFactory);
//        item.setScheduleItemParities(
//                node.getChildren()
//                        .stream()
//                        .filter(parity -> StringUtils.isNotEmpty(parity.getSubject()))
//                        .map(childConverter::convert)
//                        .filter(parity -> parity.getSubject() != null)
//                        .collect(Collectors.toSet())
//        );
//
//        for(ScheduleItemParity itemParity : item.getScheduleItemParities()) {
//            itemParity.setScheduleItem(item);
//        }
//
//        return item;
//    }
//}
