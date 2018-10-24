package ru.bmstu.schedule.dbtools;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.bmstu.schedule.entity.*;
import ru.bmstu.schedule.parser.ScheduleService;

import javax.naming.ConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

public class FillSchedule {
    private static Properties props = null;
    private static String confFile;

    public static void main(String[] args) {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        confFile = args[0];
        try {
            props = loadConf(confFile);
            PropertyKey.validateProperties(props);
            ScheduleService svc = new ScheduleService(props.getProperty(PropertyKey.SCHEDULE_BASE_URL));

            removeEntities(
                    sessionFactory,
                    Classroom.class,
                    Lecturer.class,
                    Specialization.class,
                    Department.class,
                    Faculty.class,
                    Term.class,
                    ClassTime.class,
                    EduDegree.class,
                    DayOfWeak.class,
                    ClassType.class
            );

            DBUtils.fillFromCsv(ClassType.class, sessionFactory, csvByKey(PropertyKey.REF_CLASS_TYPE));
            DBUtils.fillFromCsv(DayOfWeak.class, sessionFactory, csvByKey(PropertyKey.REF_WEAKS));
            DBUtils.fillFromCsv(EduDegree.class, sessionFactory, csvByKey(PropertyKey.REF_DEGREES));
            DBUtils.fillFromCsv(ClassTime.class, sessionFactory, csvByKey(PropertyKey.REF_CLASS_TIME));
            DBUtils.fillTerms(sessionFactory, svc);
            DBUtils.fillFacultiesAndDepartments(sessionFactory, csvByKey(PropertyKey.REF_DEPARTMENTS), svc);
            DBUtils.fillSpecializations(sessionFactory, csvByKey(PropertyKey.REF_SPECS));
            DBUtils.fillFromCsv(Lecturer.class, sessionFactory, csvByKey(PropertyKey.REF_LECTURERS));
            DBUtils.fillClassRooms(sessionFactory, svc); // !!!

        } catch (IOException | ConfigurationException e) {
            e.printStackTrace();
        } finally {
            sessionFactory.close();
        }
    }

    private static Properties loadConf(String confFile) throws IOException {
        Properties props = new Properties();
        InputStream input = new FileInputStream(confFile);
        props.load(input);

        return props;
    }

    private static String csvByKey(String key) {
        return Paths.get(confFile).getParent().toString() + props.getProperty(key);
    }

    private static void removeEntities(SessionFactory sessionFactory, Class<?> ... classes) {
        Session session = sessionFactory.openSession();

        session.beginTransaction();

        for(Class<?> clazz : classes) {
            String queryStr = "delete " + clazz.getName();
            Query query = session.createQuery(queryStr);
            query.executeUpdate();
        }

        session.getTransaction().commit();
    }

}
