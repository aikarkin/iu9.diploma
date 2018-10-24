package ru.bmstu.schedule.parser.node;

import org.jsoup.select.Elements;
import ru.bmstu.schedule.parser.GroupDeserializer;
import ru.bmstu.schedule.parser.commons.BasicNode;

import java.util.Objects;

public class CourseNode extends BasicNode<DepartmentNode, GroupNode> {
    private int courseNumber;

    public CourseNode(int courseNumber) {
        this.courseNumber = courseNumber;
    }

    public int getCourseNumber() {
        return courseNumber;
    }

    @Override
    public void parseChildren(Elements elements) {
        super.parseChildren(GroupDeserializer.class, elements);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseNode that = (CourseNode) o;
        return courseNumber == that.courseNumber;
    }

    @Override
    public int hashCode() {

        return Objects.hash(courseNumber);
    }

    @Override
    public String toString() {
        return String.format("%d курс", getCourseNumber());

    }
}
