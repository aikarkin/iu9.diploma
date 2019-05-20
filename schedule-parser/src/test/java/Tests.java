import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.bmstu.schedule.html.node.GroupNode;
import ru.bmstu.schedule.html.node.ScheduleDayNode;
import ru.bmstu.schedule.html.parser.ScheduleParser;

import java.io.IOException;
import java.util.List;

public class Tests {
    private static SessionFactory sessionFactory;

    @BeforeAll
    public static void beforeStart() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @AfterAll
    public static void afterStart() {
        sessionFactory.close();
    }

    @Test
    public void testGroupParsing() throws IOException {
        ScheduleParser parser = new ScheduleParser("https://students.bmstu.ru");

        List<ScheduleDayNode> groupIU9 = parser.scheduleFor("19024e52-8781-11e4-9a83-005056960017");

        System.out.println(groupIU9.get(0).toString());
        System.out.println(groupIU9.get(0).getChildren().get(2).toString());
        System.out.println(groupIU9.get(0).getChildren().get(2).getChildren());
    }




}
