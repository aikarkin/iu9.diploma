package ru.bmstu.schedule.dao;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import ru.bmstu.schedule.entity.Lecturer;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LecturerDao extends HibernateDao<Integer, Lecturer> {

    private static final String UNKNOWN_LEC_NAME = "[UNKNOWN]";

    public LecturerDao(SessionFactory factory) {
        super(factory);
    }

    @SuppressWarnings("unchecked")
    public List<Lecturer> findByInitials(String initials) {
        if (initials == null)
            return Collections.emptyList();

        Pattern ptr = Pattern.compile("(\\p{Lu}\\p{L}+) (\\p{Lu})[.] (\\p{Lu})[.]");
        Matcher matcher = ptr.matcher(initials);

        if (matcher.matches() && matcher.groupCount() == 3) {
            String lastName = matcher.group(1);
            String fnLetter = matcher.group(2);
            String mnLetter = matcher.group(3);

            return composeInTransaction(session -> {
                Criteria criteria = createEntityCriteria();
                criteria.add(Restrictions.eq("lastName", lastName));
                criteria.add(Restrictions.like("firstName", fnLetter + "%"));
                criteria.add(Restrictions.like("middleName", mnLetter + "%"));

                return (List<Lecturer>) criteria.list();
            });

        } else {
            return Collections.emptyList();
        }
    }

    public Optional<Lecturer> findFirstByInitials(String initials) {
        List<Lecturer> found = findByInitials(initials);
        return Optional.ofNullable(found.size() > 0 ? found.get(0) : null);
    }

    public Lecturer fetchUnknownLecturer() {
        Optional<Lecturer> lecOpt = findUniqueByProperty("lastName", UNKNOWN_LEC_NAME);
        Lecturer lec;
        if(!lecOpt.isPresent()) {
            lec = new Lecturer();
            lec.setLastName(UNKNOWN_LEC_NAME);
            lec.setMiddleName(UNKNOWN_LEC_NAME);
            lec.setFirstName(UNKNOWN_LEC_NAME);
            Integer lecId = create(lec);
            lec.setId(lecId);
        } else {
            lec = lecOpt.get();
        }

        return lec;
    }

}
