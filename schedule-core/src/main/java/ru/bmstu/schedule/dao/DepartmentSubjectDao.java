package ru.bmstu.schedule.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import ru.bmstu.schedule.entity.Department;
import ru.bmstu.schedule.entity.DepartmentSubject;
import ru.bmstu.schedule.entity.Subject;

import java.util.Optional;

public class DepartmentSubjectDao extends HibernateDao<Integer, DepartmentSubject> {

    public DepartmentSubjectDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<DepartmentSubject> findByDepartmentAndSubject(Department department, Subject subject) {
        return Optional.ofNullable(
                composeInTransaction(session -> {
                    Criteria criteria = session.createCriteria(DepartmentSubject.class);
                    criteria.add(Restrictions.eq("department", department));
                    criteria.add(Restrictions.eq("subject", subject));

                    return (DepartmentSubject) criteria.uniqueResult();
                })
        );
    }

    public Optional<DepartmentSubject> findByDepartmentCipherAndSubjectName(String departmentCipher, String subjectName) {
        return Optional.ofNullable(
                composeInTransaction(session -> {
                    Query query = session.createQuery("SELECT deptSpec FROM DepartmentSubject deptSpec " +
                            "LEFT JOIN  deptSpec.department dept " +
                            "LEFT JOIN deptSpec.department.faculty fact " +
                            "LEFT JOIN deptSpec.subject subj " +
                            "WHERE CONCAT(fact.cipher, CAST(dept.number AS string)) = :department " +
                            "AND subj.name = :subject ");
                    query.setParameter("department", departmentCipher);
                    query.setParameter("subject", subjectName);

                    return (DepartmentSubject) query.uniqueResult();
                })
        );
    }

}
