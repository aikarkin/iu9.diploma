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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CalendarFillingTest {
    private static SessionFactory sessionFactory;
    private static ScheduleParser scheduleParser;
    public static final String CALENDAR_FILE = "/home/alex/dev/src/iu9/db-course-work/java/schedule/dbtools/src/main/resources/references/calendar/csv/ИБМ5_27.03.05_2018_1.csv";

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

    @Test
    public void testCalendarFilenameRegex() {
        Pattern cfnPtr = Pattern.compile("(\\p{Lu}+\\d+)(\\p{Lu})?_(\\d+[.]\\d+[.]\\d+)_(\\d{4})[.]csv");
        String fn1 = "ИБМ6Б_01.05.18_2018.csv";
        String fn2 = "МТ5_1.2.14_2015.csv";

        Matcher mfn1 = cfnPtr.matcher(fn1);
        Matcher mfn2 = cfnPtr.matcher(fn2);

        assertTrue(mfn1.matches() && mfn1.groupCount() == 4);
        assertEquals("ИБМ6", mfn1.group(1));
        assertEquals("Б", mfn1.group(2));
        assertEquals("01.05.18", mfn1.group(3));
        assertEquals("2018", mfn1.group(4));

        assertTrue(mfn2.matches() && mfn2.groupCount() == 4);
        assertEquals("МТ5", mfn2.group(1));
        assertNull(mfn2.group(2));
        assertEquals("1.2.14", mfn2.group(3));
        assertEquals("2015", mfn2.group(4));
    }
}
