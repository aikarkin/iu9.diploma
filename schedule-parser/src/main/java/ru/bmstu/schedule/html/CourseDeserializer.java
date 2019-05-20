package ru.bmstu.schedule.html;

import org.jsoup.nodes.Element;
import ru.bmstu.schedule.html.commons.ElementDeserializer;
import ru.bmstu.schedule.html.selector.CourseSelector;
import ru.bmstu.schedule.html.node.CourseNode;

public class CourseDeserializer extends ElementDeserializer<CourseNode> {
    public CourseDeserializer(Element element) {
        super(element);
    }

    @Override
    public CourseNode deserialize() {
        String title = elementHolder().getText(CourseSelector.courseName);
        CourseNode course = new CourseNode(parseNoOfCourseFromTitle(title));
        course.parseChildren(elementHolder().getElements(CourseSelector.groups));

        return course;
    }

    private static int parseNoOfCourseFromTitle(String title) {
        try {
            return (title != null && title.length() > 0) ? Integer.parseInt(title.split(" ")[0]) : -1;
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }
}
