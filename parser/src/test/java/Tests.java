import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.bmstu.schedule.html.node.DepartmentNode;
import ru.bmstu.schedule.html.node.GroupNode;
import ru.bmstu.schedule.html.parser.ScheduleParser;

import java.io.IOException;

public class Tests {

    private static SessionFactory sessionFactory;

    @BeforeClass
    public static void beforeStart() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @AfterClass
    public static void afterStart() {
        sessionFactory.close();
    }

    @Test
    public void testGroupParsing() throws IOException {
        ScheduleParser parser = new ScheduleParser("https://students.bmstu.ru");

        System.out.println("Groups: ");
        for(GroupNode g : parser.getAllGroups()) {
            System.out.println(g);
        }
    }
}
