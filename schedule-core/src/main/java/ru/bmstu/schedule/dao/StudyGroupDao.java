package ru.bmstu.schedule.dao;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import ru.bmstu.schedule.entity.*;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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

    public Optional<StudyGroup> findByCipher(String cipher) {
        // facultyCipher,   department number,  term number,    group number,   degree
        Pattern ptr = Pattern.compile("(\\p{Lu}+)(\\d+)?-(\\d+?)(\\d)(\\p{Lu})?");
        Matcher matcher = ptr.matcher(cipher);

        if(!matcher.matches() || matcher.groupCount() != 5 || matcher.group(1) == null || matcher.group(3) == null || matcher.group(4) == null)
            return Optional.empty();

//        return composeInTransaction(session -> {
//            Optional<Integer> termOpt = tryParseInt(matcher.group(3));
//            Optional<Integer> groupNoOpt = tryParseInt(matcher.group(4));
//
//            if(!termOpt.isPresent() || !groupNoOpt.isPresent())
//                return Optional.empty();
//
//            String facultyCipher = matcher.group(1);
//            int depNumber = tryParseInt(matcher.group(2)).orElse(1);
//            int term = termOpt.get();
//            String degree = matcher.group(5) == null ? "С" : matcher.group(5);
//
//
//            Criteria facultyCriteria = session.createCriteria(Faculty.class),
//                    depCriteria = session.createCriteria(Department.class),
//                    termCriteria = session.createCriteria(Term.class),
//                    degreeCriteria = session.createCriteria(EduDegree.class),
//                    specCriteria = session.createCriteria(Specialization.class),
//                    flowCriteria = session.createCriteria(StudyFlow.class),
//                    groupCriteria = createEntityCriteria();
//
//
//            degreeCriteria.add(Restrictions.ilike("name", degree + "%"));
//            depCriteria.add(Restrictions.eq("number", depNumber));
//            depCriteria.add(Property.forName("faculty").in(facultyCriteria.list()));
//            specCriteria.add(Property.forName("eduDegree").in(degreeCriteria.list()));
//            termCriteria.add(Restrictions.eq("number", term));
//            facultyCriteria.add(Restrictions.eq("cipher", facultyCipher));
//
//            try {
//                Criteria depSpecCriteria = session.createCriteria(Class.forName("ru.bmstu.schedule.entity.DepartmentSpecialization"));
//                depSpecCriteria.add(Property.forName("department").in(depCriteria.list()));
//                depSpecCriteria.add(Property.forName("specialization").in(specCriteria.list()));
//                flowCriteria.add(Property.forName("departmentSpecialization").in(depSpecCriteria.list()));
//                groupCriteria.add(Property.forName("studyFlow").in(flowCriteria.list()));
//
//                groupCriteria.add(Property.forName("term").in(termCriteria.list()));
//                groupCriteria.add(Restrictions.eq("number", groupNoOpt.get()));
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//
//            return Optional.ofNullable(groupCriteria.list().size() == 0 ? null : (StudyGroup)groupCriteria.list().get(0));
//        });

        Stream<StudyGroup> stream = filter(group -> {
            Optional<Integer> termOpt = tryParseInt(matcher.group(3));
            Optional<Integer> groupNoOpt = tryParseInt(matcher.group(4));

            if(!termOpt.isPresent() || !groupNoOpt.isPresent())
                return false;

            String facultyCipher = matcher.group(1);
            int depNumber = tryParseInt(matcher.group(2)).orElse(1);
            int term = termOpt.get();
            String degree = matcher.group(5) == null ? "c" : matcher.group(5).toLowerCase();

            return group.getNumber() == groupNoOpt.get()
                    && group.getTerm().getNumber() == term
                    && group.getStudyFlow().getSpecialization().getEduDegree().getName().charAt(0) == degree.charAt(0)
                    && group.getStudyFlow().getDepartment().getFaculty().getCipher().equals(facultyCipher)
                    && group.getStudyFlow().getDepartment().getNumber() == depNumber;
        });

        return stream.findFirst();
    }

    Optional<Integer> tryParseInt(String val) {
        Integer intVal = null;
        try {
             intVal = Integer.parseInt(val);
        } catch (NumberFormatException ignored) { }

        return Optional.ofNullable(intVal);
    }

}
