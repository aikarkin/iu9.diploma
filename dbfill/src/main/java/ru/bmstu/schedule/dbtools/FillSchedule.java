package ru.bmstu.schedule.dbtools;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.bmstu.schedule.csv.CSVUtils;
import ru.bmstu.schedule.dao.*;
import ru.bmstu.schedule.entity.*;
import ru.bmstu.schedule.html.parser.ScheduleParser;

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
        } catch (IOException | ConfigurationException | ClassNotFoundException e) {
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

    private static void clearData() throws ClassNotFoundException {
        removeEntities(
                sessionFactory,
//                Classroom.class,
                Lecturer.class,
                HoursPerClass.class,
                CalendarItemCell.class,
                CalendarItem.class,
                ScheduleItemParity.class,
                ScheduleItem.class,
                ScheduleDay.class,
                StudyGroup.class,
                StudyFlow.class,
                ClassTime.class,
                DayOfWeak.class,
                ClassType.class,
                Subject.class,
                Class.forName("ru.bmstu.schedule.entity.DepartmentSpecialization"),
                Specialization.class,
                Department.class,
                Faculty.class,
                EduDegree.class,
                Term.class
        );

    }

    private static void fillData() throws IOException {
        ScheduleParser scheduleParser = new ScheduleParser(props.getProperty(PropertyKey.SCHEDULE_BASE_URL));

        CSVUtils.fillFromCsv(new ClassTypeDao(sessionFactory), pathByKey(PropertyKey.REF_CLASS_TYPE));
        CSVUtils.fillFromCsv(new WeakDao(sessionFactory), pathByKey(PropertyKey.REF_WEAKS));
        CSVUtils.fillFromCsv(new EduDegreeDao(sessionFactory), pathByKey(PropertyKey.REF_DEGREES));
        CSVUtils.fillFromCsv(new ClassTimeDao(sessionFactory), pathByKey(PropertyKey.REF_CLASS_TIME));

        DBUtils.fillTerms(sessionFactory, scheduleParser);
        DBUtils.fillFacultiesAndDepartments(sessionFactory, pathByKey(PropertyKey.REF_DEPARTMENTS), scheduleParser);
        DBUtils.fillSpecializations(sessionFactory, pathByKey(PropertyKey.REF_SPECS));
        DBUtils.fillDepToSpec(sessionFactory, pathByKey(PropertyKey.REF_SPECDEPS));
        DBUtils.fillStudyFlows(sessionFactory, scheduleParser);
        DBUtils.fillCalendars(sessionFactory, pathByKey(PropertyKey.REF_FOLDER_CALENDAR));
//        DBUtils.fillClassRooms(sessionFactory, scheduleParser);

        CSVUtils.fillFromCsv(new LecturerDao(sessionFactory), pathByKey(PropertyKey.REF_LECTURERS));

        DBUtils.fillSchedule(sessionFactory, scheduleParser);
    }

    private static void removeEntities(SessionFactory sessionFactory, Class<?> ... classes) {
        Session session = sessionFactory.openSession();

        session.beginTransaction();

        for (Class<?> clazz : classes) {
            try {
                String queryStr = "delete " + clazz.getName();
                Query query = session.createQuery(queryStr);
                query.executeUpdate();
            } catch (Exception e) {
                System.out.println("[error] Unable to remove entities of type: " + clazz.getName());
                e.printStackTrace();
            }
        }


        session.getTransaction().commit();
    }

    private static Properties loadConf(String confFile) throws IOException {
        Properties props = new Properties();
        InputStream input = new FileInputStream(confFile);
        props.load(input);

        return props;
    }

    private static String pathByKey(String key) {
        return Paths.get(confFile).getParent().toString() + props.getProperty(key);
    }

}
