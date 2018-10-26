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
    private static SessionFactory sessionFactory;

    public static void main(String[] args) {
        try {
            init(args[0]);
            clearData();
            fillData();
        } catch (IOException | ConfigurationException e) {
            e.printStackTrace();
        } finally {
            sessionFactory.close();
        }
    }

    private static void init(String file) throws IOException, ConfigurationException {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        confFile = file;
        props = loadConf(file);
        PropertyKey.validateProperties(props);
    }

    private static void clearData() {
        Session session = sessionFactory.openSession();

        Query remDepSpec = session.createQuery("delete from DepartmentSpecialization");
        remDepSpec.executeUpdate();

        session.close();

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
    }

    private static void fillData() throws IOException {
        ScheduleService svc = new ScheduleService(props.getProperty(PropertyKey.SCHEDULE_BASE_URL));

        DBUtils.fillFromCsv(ClassType.class, sessionFactory, csvByKey(PropertyKey.REF_CLASS_TYPE));
        DBUtils.fillFromCsv(DayOfWeak.class, sessionFactory, csvByKey(PropertyKey.REF_WEAKS));
        DBUtils.fillFromCsv(EduDegree.class, sessionFactory, csvByKey(PropertyKey.REF_DEGREES));
        DBUtils.fillFromCsv(ClassTime.class, sessionFactory, csvByKey(PropertyKey.REF_CLASS_TIME));
        DBUtils.fillTerms(sessionFactory, svc);
        DBUtils.fillFacultiesAndDepartments(sessionFactory, csvByKey(PropertyKey.REF_DEPARTMENTS), svc);
        DBUtils.fillSpecializations(sessionFactory, csvByKey(PropertyKey.REF_SPECS));
        DBUtils.fillDepToSpec(sessionFactory, csvByKey(PropertyKey.REF_SPECDEPS));

//        DBUtils.fillFromCsv(Lecturer.class, sessionFactory, csvByKey(PropertyKey.REF_LECTURERS));
//        DBUtils.fillClassRooms(sessionFactory, svc);

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

    private static Properties loadConf(String confFile) throws IOException {
        Properties props = new Properties();
        InputStream input = new FileInputStream(confFile);
        props.load(input);

        return props;
    }

    private static String csvByKey(String key) {
        return Paths.get(confFile).getParent().toString() + props.getProperty(key);
    }

}
