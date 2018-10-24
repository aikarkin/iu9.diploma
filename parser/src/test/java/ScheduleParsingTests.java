import org.junit.BeforeClass;
import org.junit.Test;
import ru.bmstu.schedule.entity.Department;
import ru.bmstu.schedule.parser.ScheduleService;
import ru.bmstu.schedule.parser.commons.BasicNode;
import ru.bmstu.schedule.parser.commons.Node;
import ru.bmstu.schedule.parser.commons.NodeTraveller;
import ru.bmstu.schedule.parser.node.*;


import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

public class ScheduleParsingTests {
    private static ScheduleService svc;

    @BeforeClass
    public static void init() throws IOException {
        svc = new ScheduleService("https://students.bmstu.ru/");
    }

    @Test
    public void testScheduleListParsing() {
        for(FacultyNode faculty : svc.getFaculties()) {
            System.out.println("Faculty: " + faculty);
            for(DepartmentNode dept : faculty.getChildren()) {
                System.out.println("\t->department: " + dept);
                for(CourseNode course : dept.getChildren()) {
                    System.out.println("\t\t->course: " + course);
                    for(GroupNode group : course.getChildren()) {
                        System.out.println("\t\t\t->group: " + group + "\t -> " + group.getScheduleLink());
                    }
                }
            }
        }
    }

    @Test
    public void testScheduleDayParsing() throws IOException {
        GroupNode group = svc.getFaculties().get(1)
                .getChildren().get(1) // first department
                .getChildren().get(0) // first course
                .getChildren().get(2); // first group

        System.out.println("Group: " + group.toString() + ": " + group.getScheduleLink());


        svc.scheduleFor(group).forEach(this::printScheduleDay);
    }

    @Test
    public void testScheduleByGroupId() throws IOException {
        svc.scheduleFor("МТ2-11М").forEach(this::printScheduleDay);
    }

    private void printScheduleDay(ScheduleDayNode day) {
        System.out.println("-> " + day.getDayOfWeak().getWeakName().toUpperCase() + "\n");

        for (ScheduleItemNode itemNode : day.getChildren()) {
            System.out.println(itemNode.getStartsAt().toString() + " - " + itemNode.getEndsAt().toString());

            for (ScheduleItemParityNode parity : itemNode.getChildren()) {
                System.out.printf("\t%s - {(%s) %s %s\t %s}\n", parity.getDayParity(), parity.getClassType(), parity.getSubject(), parity.getClassroom(), parity.getLecturer());
            }
        }
        System.out.println();
    }

    @Test
    public void test1() {
        NodeTraveller traveller = new NodeTraveller(new FacultiesListNode(svc.getScheduleListDoc()));
        for(DepartmentNode g : traveller.filter(DepartmentNode.class, (g) -> true)) {
            System.out.println(g);
        }
    }
}
