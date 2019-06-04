package ru.bmstu.schedule.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.Department;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DepartmentDao extends HibernateDao<Integer, Department> {

    private static final Map<String, String> DEPARTMENTS_WITHOUT_FACULTIES;

    static {
        DEPARTMENTS_WITHOUT_FACULTIES = new HashMap<>();

        DEPARTMENTS_WITHOUT_FACULTIES.put("ФВ", "Физическое воспитание");
        DEPARTMENTS_WITHOUT_FACULTIES.put("ЮР", "Юриспруденция, интеллектуальная собственность и судебная экспертиза");

    }

    public DepartmentDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<Department> findByTitle(String title) {
        return findUniqueByProperty("title", title);
    }

    public Optional<Department> findByCipher(String cipher) {
        if (cipher == null)
            return Optional.empty();

        Pattern cipherPtr = Pattern.compile("(\\p{Lu}+)(\\d+)?");
        Matcher cipherMatcher = cipherPtr.matcher(cipher);

        if (!cipherMatcher.matches() || cipherMatcher.groupCount() != 2)
            return Optional.empty();

        if (cipherMatcher.group(2) == null) {
            String deptCode = cipherMatcher.group(1);
            if (!DEPARTMENTS_WITHOUT_FACULTIES.containsKey(deptCode))
                return Optional.empty();
            String deptName = DEPARTMENTS_WITHOUT_FACULTIES.get(deptCode);

            return findByTitle(deptName);
        }

        return Optional.ofNullable(
                composeInTransaction(session -> {
                    Query query = session.createQuery(
                            "SELECT dept FROM Department dept " +
                                    "LEFT JOIN dept.faculty fact " +
                                    "WHERE CONCAT(fact.cipher, CAST(dept.number AS string)) = :cipher"
                    );
                    query.setParameter("cipher", cipher);
                    return (Department) query.uniqueResult();
                })
        );
    }

}
