package ru.bmstu.schedule.dbtools;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.bmstu.schedule.csv.CSVUtils;
import ru.bmstu.schedule.dao.ClassTimeDao;
import ru.bmstu.schedule.dao.ClassTypeDao;
import ru.bmstu.schedule.dao.WeekDao;
import ru.bmstu.schedule.entity.*;
import ru.bmstu.schedule.html.parser.ScheduleParser;

import javax.naming.ConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Properties;


public class FillSchedule {

    private static final String DEFAULT_CONFIG_FILE = "config.properties";
    private static Properties props = null;
    private static String confFile;
    private static SessionFactory sessionFactory;

    public static void main(String[] args) {
        try {
            init(args);
            clearData();
            fillData();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sessionFactory.close();
        }
    }

    private static void init(String[] args) throws IOException, ConfigurationException {
        sessionFactory = new Configuration().configure().buildSessionFactory();

        if (args.length == 0) {
            URL defaultConfUrl = FillSchedule.class.getClassLoader().getResource(DEFAULT_CONFIG_FILE);
            if (defaultConfUrl == null) {
                throw new RuntimeException("Default config resource not found: " + DEFAULT_CONFIG_FILE);
            }
            confFile = defaultConfUrl.getFile();
        } else {
            confFile = args[0];
        }

        props = loadConf(confFile);
        PropertyKey.validateProperties(props);
    }

    private static void clearData() throws ClassNotFoundException {
        removeEntities(
                sessionFactory,
                Classroom.class,
                HoursPerClass.class,
                CalendarItemCell.class,
                CalendarItem.class,
                ScheduleItemParity.class,
                ScheduleItem.class,
                ScheduleDay.class,
                StudyGroup.class,
                Calendar.class,
                LecturerSubject.class,
                DepartmentSubject.class,
                Lecturer.class,
                ClassTime.class,
                DayOfWeek.class,
                ClassType.class,
                Subject.class,
                Class.forName("ru.bmstu.schedule.entity.DepartmentSpecialization"),
                Specialization.class,
                Speciality.class,
                Department.class,
                Faculty.class,
                EduDegree.class,
                Term.class
        );

    }

    private static void fillData() throws IOException {
        ScheduleParser scheduleParser = new ScheduleParser(props.getProperty(PropertyKey.SCHEDULE_BASE_URL));

        // Fill common entities from csv-references & https://students.bmstu.ru/schedule/:
        CSVUtils.fillFromCsv(new ClassTypeDao(sessionFactory), pathByKey(PropertyKey.REF_CLASS_TYPE));
        CSVUtils.fillFromCsv(new WeekDao(sessionFactory), pathByKey(PropertyKey.REF_WEEKS));
        CSVUtils.fillFromCsv(new ClassTimeDao(sessionFactory), pathByKey(PropertyKey.REF_CLASS_TIME));
        DBUtils.fillSpecializationsAndDegrees(sessionFactory, pathByKey(PropertyKey.REF_SPECS));
        DBUtils.fillFaculties(sessionFactory, pathByKey(PropertyKey.REF_FACULTIES));
        DBUtils.fillDepartments(sessionFactory, pathByKey(PropertyKey.REF_DEPARTMENTS));
        DBUtils.fillClassRooms(sessionFactory, scheduleParser);
        DBUtils.fillGroups(sessionFactory, pathByKey(PropertyKey.REF_GROUPS));

        // Fill calendar plans from csv-reference:
        DBUtils.fillCalendars(sessionFactory, pathByKey(PropertyKey.REF_FOLDER_CALENDAR));

        // Fill lecturers after calendar filling, because in other case we haven't subjects
        CSVUtils.fillLecturers(pathByKey(PropertyKey.REF_LECTURERS), sessionFactory);

        DBUtils.fillLecturerSubjects(sessionFactory, pathByKey(PropertyKey.REF_SUBJECTS));
    }

    private static void removeEntities(SessionFactory sessionFactory, Class<?>... classes) {
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
        InputStream input = new FileInputStream(new File(confFile));
        props.load(input);

        return props;
    }

    private static String pathByKey(String key) {
        return Paths.get(confFile).getParent().toString() + props.getProperty(key);
    }

}
