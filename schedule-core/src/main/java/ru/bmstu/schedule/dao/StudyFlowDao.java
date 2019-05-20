package ru.bmstu.schedule.dao;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import ru.bmstu.schedule.entity.Department;
import ru.bmstu.schedule.entity.Specialization;
import ru.bmstu.schedule.entity.StudyFlow;

import java.util.List;
import java.util.Optional;

public class StudyFlowDao extends HibernateDao<Integer, StudyFlow> {

    public StudyFlowDao(SessionFactory factory) {
        super(factory);
    }

    @SuppressWarnings("unchecked")
    public Optional<StudyFlow> findByYearDepartmentAndSpecialization
            (int year, String departmentCipher, String specializationCode) {
        SpecializationDao specDoa = new SpecializationDao(getSessionFactory());
        DepartmentDao depDoa = new DepartmentDao(getSessionFactory());
        Optional<Specialization> spec = specDoa.findByCode(specializationCode);
        Optional<Department> dep = depDoa.findByCipher(departmentCipher);

        if(spec.isPresent() && dep.isPresent()) {
            return filter(flow ->
                            flow.getSpecialization().equals(spec.get())
                            && flow.getDepartment().equals(dep.get())
                            && flow.getEnrollmentYear() == year)
                    .findFirst();
        }

        return Optional.empty();
    }
}
