package ru.bmstu.schedule.dao;

import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.Department;

import java.util.List;
import java.util.Optional;

public class DepartmentDao extends HibernateDao<Integer, Department> {
    public DepartmentDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<Department> findByCipher(String cipher) {
        final String target = new String(new char[] {cipher.charAt(cipher.length() - 1)}).matches("\\d")
                ? cipher
                : cipher + "1";

        List<Department> found = filter(dep -> dep.getCipher().equals(target));
        return Optional.ofNullable(found.size() > 0 ? found.get(0) : null);
    }
}
