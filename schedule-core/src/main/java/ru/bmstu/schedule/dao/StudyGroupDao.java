package ru.bmstu.schedule.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import ru.bmstu.schedule.entity.StudyGroup;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudyGroupDao extends HibernateDao<Integer, StudyGroup> {

    public StudyGroupDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<StudyGroup> findByCipher(String cipher) {
        // (1) facultyCipher,  (2) department number,  (3) term number,  (4) group number,  (5) degree
        Pattern ptr = Pattern.compile("(\\p{Lu}+)(\\d+)?-(\\d+?)(\\d)(\\p{Lu})?");
        Matcher matcher = ptr.matcher(cipher);

        if (!matcher.matches() || matcher.groupCount() != 5 || matcher.group(1) == null || matcher.group(3) == null || matcher.group(4) == null)
            return Optional.empty();

        return Optional.ofNullable(composeInTransaction(session -> {
            Query query = session.createQuery(
                    "SELECT gr FROM StudyGroup gr " +
                            "LEFT JOIN gr.calendar c " +
                            "LEFT JOIN gr.term t " +
                            "LEFT JOIN gr.calendar.departmentSpecialization ds " +
                            "LEFT JOIN gr.calendar.departmentSpecialization.department dept " +
                            "LEFT JOIN gr.calendar.departmentSpecialization.department.faculty fact " +
                            "LEFT JOIN gr.calendar.departmentSpecialization.specialization.speciality " +
                            "LEFT JOIN gr.calendar.departmentSpecialization.specialization.speciality.degree deg " +
                            "WHERE CONCAT(fact.cipher, CAST(dept.number AS string), '-', CAST(t.number AS string), gr.number) = :group " +
                            "AND deg.name = :degree"
            );
            String grCipher;
            String degreeName;
            char degreeLetter;
            if (matcher.group(5) == null) {
                grCipher = matcher.group();
                degreeLetter = 'Б';
            } else {
                String grCipherWithDegree = matcher.group();
                grCipher = grCipherWithDegree.substring(0, grCipherWithDegree.length() - 2);
                degreeLetter = matcher.group(5).charAt(0);
            }

            switch (degreeLetter) {
                case 'А':
                    degreeName = "Исследователь. Преподаватель-исследователь";
                    break;
                case 'М':
                    degreeName = "Магистр";
                    break;
                default:
                    degreeName = "Бакалавр";
                    break;
            }
            query.setParameter("group", grCipher);
            query.setParameter("degree", degreeName);

            return (StudyGroup) query.uniqueResult();
        }));
    }

}
