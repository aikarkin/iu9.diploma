package ru.bmstu.schedule.dao;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import ru.bmstu.schedule.entity.Lecturer;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LecturerDao extends HibernateDao<Integer, Lecturer> {
    public LecturerDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<Lecturer> findByEmail(String email) {
        return findUniqueByProperty("email", email);
    }

    @SuppressWarnings("unchecked")
    public List<Lecturer> findByInitials(String initials) {
        Pattern ptr = Pattern.compile("(\\p{Lu}\\p{L}+) (\\p{Lu})[.] (\\p{Lu})[.]");
        Matcher matcher = ptr.matcher(initials);

        if(matcher.matches() && matcher.groupCount() == 3) {
            String lastName = matcher.group(1);
            String fnLetter = matcher.group(2);
            String mnLetter = matcher.group(3);

            return composeInTransaction(session -> {
                Criteria criteria = createEntityCriteria();
                criteria.add(Restrictions.eq("lastName", lastName));
                criteria.add(Restrictions.like("firstName", fnLetter + "%"));
                criteria.add(Restrictions.like("middleName", mnLetter + "%"));

                return (List<Lecturer>)criteria.list();
            });

        } else {
            String msg = String.format(
                "Invalid lecturer initials '%s'. Lecturer initials should have a form 'Lastname F. M.'",
                initials
            );
            throw new InvalidParameterException(msg);
        }
    }

    public Optional<Lecturer> findFirstByInitials(String initials) {
        List<Lecturer> found = findByInitials(initials);
        return Optional.ofNullable(found.size() > 0 ? found.get(0) : null);
    }
}
