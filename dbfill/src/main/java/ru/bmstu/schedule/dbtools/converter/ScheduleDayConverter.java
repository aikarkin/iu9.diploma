//package ru.bmstu.schedule.dbtools.converter;
//
//import org.apache.commons.lang3.StringUtils;
//import org.hibernate.SessionFactory;
//import ru.bmstu.schedule.dao.WeekDao;
//import ru.bmstu.schedule.entity.DayOfWeek;
//import ru.bmstu.schedule.entity.ScheduleDay;
//import ru.bmstu.schedule.entity.ScheduleItem;
//import ru.bmstu.schedule.html.node.ScheduleDayNode;
//import ru.bmstu.schedule.html.node.ScheduleItemNode;
//
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//public class ScheduleDayConverter implements NodeToEntityConverter<ScheduleDayNode, ScheduleDay> {
//    private WeekDao weekDao;
//    private SessionFactory sessionFactory;
//
//    public ScheduleDayConverter(SessionFactory factory) {
//        weekDao = new WeekDao(factory);
//        this.sessionFactory = factory;
//    }
//
//    @Override
//    public ScheduleDay convert(ScheduleDayNode node) {
//        Optional<DayOfWeek> weekOpt = weekDao.findByTitle(node.getDayOfWeek().getWeekName());
//
//        ScheduleDay day = new ScheduleDay();
//        weekOpt.ifPresent(day::setDayOfWeek);
//
//        ScheduleItemConverter childConverter = new ScheduleItemConverter(sessionFactory);
//        day.setScheduleItems(
//                node.getChildren()
//                .stream()
//                .filter(ScheduleDayConverter::isItemNodeNotEmpty)
//                .map(childConverter::convert)
//                .collect(Collectors.toSet())
//        );
//
//        for(ScheduleItem item : day.getScheduleItems()) {
//            item.setScheduleDay(day);
//        }
//
//        return day;
//    }
//
//    public static boolean isDayNodeNotEmpty(ScheduleDayNode dayNode) {
//        return dayNode.getChildren()
//                .stream()
//                .anyMatch(ScheduleDayConverter::isItemNodeNotEmpty);
//    }
//
//    private static boolean isItemNodeNotEmpty(ScheduleItemNode item) {
//        return item.getChildren()
//                .stream()
//                .anyMatch(parity -> StringUtils.isNotEmpty(parity.getSubject()));
//    }
//}
