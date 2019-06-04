package ru.bmstu.schedule.dao;

import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.StudyGroup;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class StudyGroupDao extends HibernateDao<Integer, StudyGroup> {

    public StudyGroupDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<StudyGroup> findByCipher(String cipher) {
        // facultyCipher,   department number,  term number,    group number,   degree
        Pattern ptr = Pattern.compile("(\\p{Lu}+)(\\d+)?-(\\d+?)(\\d)(\\p{Lu})?");
        Matcher matcher = ptr.matcher(cipher);

        if (!matcher.matches() || matcher.groupCount() != 5 || matcher.group(1) == null || matcher.group(3) == null || matcher.group(4) == null)
            return Optional.empty();

        Stream<StudyGroup> stream = filter(group -> {
            Optional<Integer> termOpt = tryParseInt(matcher.group(3));
            Optional<Integer> groupNoOpt = tryParseInt(matcher.group(4));

            if (!termOpt.isPresent() || !groupNoOpt.isPresent())
                return false;

            String facultyCipher = matcher.group(1);
            int depNumber = tryParseInt(matcher.group(2)).orElse(1);
            int term = termOpt.get();
            String degree = matcher.group(5) == null ? "c" : matcher.group(5).toLowerCase();

            return group.getNumber() == groupNoOpt.get()
                    && group.getTerm().getNumber() == term
                    && group.getCalendar().getDepartmentSpecialization().getSpecialization().getSpeciality().getDegree().getName().charAt(0) == degree.charAt(0)
                    && group.getCalendar().getDepartmentSpecialization().getDepartment().getFaculty().getCipher().equals(facultyCipher)
                    && group.getCalendar().getDepartmentSpecialization().getDepartment().getNumber() == depNumber;
        });

        return stream.findFirst();
    }

    private Optional<Integer> tryParseInt(String val) {
        Integer intVal = null;
        try {
            intVal = Integer.parseInt(val);
        } catch (NumberFormatException ignored) {
        }

        return Optional.ofNullable(intVal);
    }

}
