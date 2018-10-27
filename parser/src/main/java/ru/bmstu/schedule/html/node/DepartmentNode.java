package ru.bmstu.schedule.html.node;

import org.jsoup.select.Elements;
import ru.bmstu.schedule.html.CourseDeserializer;
import ru.bmstu.schedule.html.commons.BasicNode;

import java.util.Objects;

public class DepartmentNode extends BasicNode<FacultyNode, CourseNode> {
    private String facultyCipher;
    private int departmentNumber;

    public DepartmentNode(String facultyCipher, int departmentNumber) {
        this.facultyCipher = facultyCipher;
        this.departmentNumber = departmentNumber;
    }

    public String getFacultyCipher() {
        return facultyCipher;
    }


    public int getDepartmentNumber() {
        return departmentNumber;
    }

    public String getCipher() {
        return facultyCipher + (departmentNumber == 0 ? "" : String.valueOf(departmentNumber));
    }

    @Override
    public void parseChildren(Elements elements) {
        super.parseChildren(CourseDeserializer.class, elements);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepartmentNode that = (DepartmentNode) o;
        return departmentNumber == that.departmentNumber &&
                Objects.equals(facultyCipher, that.facultyCipher);
    }

    @Override
    public int hashCode() {

        return Objects.hash(facultyCipher, departmentNumber);
    }


    @Override
    public String toString() {
        return this.getCipher();

    }
}
