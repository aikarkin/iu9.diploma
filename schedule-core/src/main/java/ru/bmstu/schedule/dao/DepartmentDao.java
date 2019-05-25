package ru.bmstu.schedule.dao;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import ru.bmstu.schedule.entity.Department;
import ru.bmstu.schedule.entity.Faculty;

import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DepartmentDao extends HibernateDao<Integer, Department> {

    public DepartmentDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<Department> findByCipher(String cipher) {
        Pattern cipherPtr = Pattern.compile("(\\p{Lu}+)(\\d+)");
        Matcher cipherMatcher = cipherPtr.matcher(cipher);

        if (!cipherMatcher.matches() || cipherMatcher.groupCount() != 2)
            return Optional.empty();

        String facultyCipher = cipherMatcher.group(1);
        int depNumber = Integer.parseInt(cipherMatcher.group(2));

        getSession().beginTransaction();

        Criteria factCriteria = getSession().createCriteria(Faculty.class);
        factCriteria.add(Restrictions.eq("cipher", facultyCipher));

        Criteria cipherCriteria = createEntityCriteria();
        cipherCriteria.add(Property.forName("faculty").in(factCriteria.list()));
        cipherCriteria.add(Restrictions.eq("number", depNumber));

        List<Department> found = cipherCriteria.list();

        getSession().getTransaction().commit();

        return found.stream().findFirst();
    }

}
