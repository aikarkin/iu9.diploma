package ru.bmstu.schedule.dao;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import ru.bmstu.schedule.entity.Department;
import ru.bmstu.schedule.entity.DepartmentSpecialization;
import ru.bmstu.schedule.entity.Specialization;

import java.util.Optional;

public class DepartmentSpecializationDao extends HibernateDao<Integer, DepartmentSpecialization> {

    public DepartmentSpecializationDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<DepartmentSpecialization> findByDepartmentAndSpecialization(Department department, Specialization specialization) {
        if (department == null || specialization == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(
                composeInTransaction(session -> {
                    Criteria criteria = session.createCriteria(DepartmentSpecialization.class);
                    criteria.add(Restrictions.eq("department", department));
                    criteria.add(Restrictions.eq("specialization", specialization));

                    return (DepartmentSpecialization) criteria.uniqueResult();
                })
        );
    }

}
