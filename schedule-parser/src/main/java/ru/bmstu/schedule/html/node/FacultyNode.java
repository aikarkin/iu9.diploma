package ru.bmstu.schedule.html.node;

import org.jsoup.select.Elements;
import ru.bmstu.schedule.html.DepartmentDeserializer;
import ru.bmstu.schedule.html.commons.RootNode;

import java.util.Objects;

public class FacultyNode extends RootNode<DepartmentNode> {
    private String title;
    private String cipher;

    public String getTitle() {
        return title;
    }

    public String getCipher() {
        return cipher;
    }

    public FacultyNode(String title, String cipher) {
        this.title = title;
        this.cipher = cipher;
    }

    @Override
    public void parseChildren(Elements elements) {
        super.parseChildren(DepartmentDeserializer.class, elements);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FacultyNode that = (FacultyNode) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(cipher, that.cipher);
    }

    @Override
    public int hashCode() {

        return Objects.hash(title, cipher);
    }

    @Override
    public String toString() {
        return "FacultyNode{" +
                "title='" + title + '\'' +
                ", cipher='" + cipher + '\'' +
                '}';
    }
}
