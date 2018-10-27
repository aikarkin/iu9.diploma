import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.bmstu.schedule.csv.CSVUtils;
import ru.bmstu.schedule.dao.StudyFlowDao;
import ru.bmstu.schedule.entity.StudyFlow;
import ru.bmstu.schedule.html.parser.ScheduleParser;

import java.io.IOException;

public class CalendarFillingTest {
    private static SessionFactory sessionFactory;
    private static ScheduleParser scheduleParser;
    public static final String CALENDAR_FILE = "/home/alex/dev/src/iu9/db-course-work/java/schedule/dbtools/src/main/resources/references/calendar/csv/ИБМ5Б_27.03.05_2018_1.csv";

    @BeforeAll
    public static void beforeAll() throws IOException {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        scheduleParser = new ScheduleParser("https://students.bmstu.ru");

        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
            session.createQuery("delete HoursPerClass").executeUpdate();
            session.createQuery("delete CalendarItemCell").executeUpdate();
            session.createQuery("delete CalendarItem").executeUpdate();
        session.getTransaction().commit();
    }

    @AfterAll
    public static void afterAll() {
        sessionFactory.close();
    }

    @Test
    public void testCalendarFilling() throws IOException {
        StudyFlowDao flowDao = new StudyFlowDao(sessionFactory);
        StudyFlow flow = flowDao.findByKey(45);
        CSVUtils.fillCalendar(flow, sessionFactory, CALENDAR_FILE);
    }
}
