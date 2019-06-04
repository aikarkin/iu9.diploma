package ru.bmstu.schedule.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.Specialization;

import java.util.Optional;

public class SpecializationDao extends HibernateDao<Integer, Specialization> {

    public SpecializationDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<Specialization> findByCode(String specializationCode) {
        return Optional.ofNullable(
                composeInTransaction(session -> {
                    Query specQuery = session.createQuery(
                            "SELECT s FROM Specialization s JOIN FETCH s.speciality " +
                                    "WHERE CONCAT(s.speciality.code, '_', CAST(s.numberInSpeciality AS string)) = :code"
                    );
                    specQuery.setParameter("code", specializationCode);
                    return (Specialization) specQuery.uniqueResult();
                })
        );
    }

}
