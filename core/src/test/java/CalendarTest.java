import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.bmstu.schedule.dao.ClassTypeDao;
import ru.bmstu.schedule.dao.StudyFlowDao;
import ru.bmstu.schedule.dao.SubjectDao;
import ru.bmstu.schedule.dao.TermDao;
import ru.bmstu.schedule.entity.*;

import java.util.Optional;

public class CalendarTest {
    private static SessionFactory sessionFactory;

    @BeforeAll
    public static void beforeTestsStarted() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.createQuery("delete HoursPerClass").executeUpdate();
        session.createQuery("delete CalendarItemCell").executeUpdate();
        session.createQuery("delete CalendarItem").executeUpdate();
        session.getTransaction().commit();

    }

    @AfterAll
    public static void afterTestsFinished() {
        sessionFactory.close();
    }

    @Test
    public void test1() {
        StudyFlowDao flowDao = new StudyFlowDao(sessionFactory);
        SubjectDao subject = new SubjectDao(sessionFactory);
        TermDao termDao = new TermDao(sessionFactory);
        ClassTypeDao ctDao = new ClassTypeDao(sessionFactory);

        StudyFlow flow = flowDao.findByKey(45);

        CalendarItem item = new CalendarItem();

        item.setSubject(subject.findByKey(4));

        CalendarItemCell itemCell = new CalendarItemCell();
        Optional<Term> termOpt = termDao.findByNumber(3);
        if(termOpt.isPresent()) {
            itemCell.setTerm(termOpt.get());
            HoursPerClass hpc = new HoursPerClass();

            hpc.setClassType(ctDao.findByKey(166));
            hpc.setNoOfHours(64);

            itemCell.addHoursPerClass(hpc);

            item.addItemCell(itemCell);
        }
        flow.addCalendarItem(item);

        flowDao.update(flow);
    }

}
