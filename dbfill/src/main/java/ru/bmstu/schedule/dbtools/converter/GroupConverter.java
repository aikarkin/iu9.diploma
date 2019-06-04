//package ru.bmstu.schedule.dbtools.converter;
//
//import org.hibernate.SessionFactory;
//import ru.bmstu.schedule.dao.TermDao;
//import ru.bmstu.schedule.entity.StudyGroup;
//import ru.bmstu.schedule.entity.Term;
//import ru.bmstu.schedule.html.node.GroupNode;
//import java.util.Optional;
//
//public class GroupConverter implements NodeToEntityConverter<GroupNode, StudyGroup> {
//    private TermDao termDao;
//
//    public GroupConverter(SessionFactory factory) {
//        this.termDao = new TermDao(factory);
//    }
//
//    @Override
//    public StudyGroup convert(GroupNode node) {
//        StudyGroup group = new StudyGroup();
//
//        Optional<Term> termOpt = termDao.findByNumber(node.getTermNumber());
//
//        termOpt.ifPresent(group::setTerm);
//        group.setNumber(node.getGroupNumber());
//
//
//        return group;
//
//    }
//}
