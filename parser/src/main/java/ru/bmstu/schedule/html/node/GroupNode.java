package ru.bmstu.schedule.html.node;

import ru.bmstu.schedule.html.commons.LeafNode;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupNode extends LeafNode<CourseNode> {
    private String facultyCipher;
    private int departmentNumber;
    private int groupNumber;
    private int termNumber;
    private String uuid;
    private Degree degree;
    private String scheduleLink;

    public GroupNode(String facultyCipher, int departmentNumber, int termNumber, int groupNumber, Degree degree) {
        this.facultyCipher = facultyCipher;
        this.departmentNumber = departmentNumber;
        this.groupNumber = groupNumber;
        this.degree = degree;
        this.termNumber = termNumber;
    }

    public String getScheduleLink() {
        return scheduleLink;
    }

    public void setScheduleLink(String scheduleLink) {
        String[] slash = scheduleLink.split("/");
        String mayBeId = slash[slash.length - 1];
        this.uuid = isUuid(mayBeId) ? mayBeId : null;
        this.scheduleLink = scheduleLink;
    }

    public String getFacultyCipher() {
        return facultyCipher;
    }

    public int getDepartmentNumber() {
        return departmentNumber;
    }

    public int getGroupNumber() {
        return groupNumber;
    }

    public Degree getDegree() {
        return degree;
    }

    public int getTermNumber() {
        return termNumber;
    }

    @Override
    public String toString() {
        return this.getCipher();
    }

    public String getUuid() {
        return uuid;
    }

    public String getCipher() {
        String groupCipher = getDepartmentNumber() == 0
                ? String.format(
                "%s-%d%d",
                getFacultyCipher(),
                getTermNumber(),
                getGroupNumber()
        )
                : String.format(
                "%s%d-%d%d",
                getFacultyCipher(),
                getDepartmentNumber(),
                getTermNumber(),
                getGroupNumber()
        );

        return (degree == Degree.BACHELOR || degree == Degree.SPECIALTY)
                ? groupCipher
                : groupCipher + degree.getFullName().toUpperCase().charAt(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupNode groupNode = (GroupNode) o;
        return departmentNumber == groupNode.departmentNumber &&
                groupNumber == groupNode.groupNumber &&
                termNumber == groupNode.termNumber &&
                Objects.equals(facultyCipher, groupNode.facultyCipher) &&
                Objects.equals(uuid, groupNode.uuid) &&
                degree == groupNode.degree;
    }

    @Override
    public int hashCode() {
        return Objects.hash(facultyCipher, departmentNumber, groupNumber, termNumber, uuid, degree);
    }

    public static boolean isUuid(String name) {
        Pattern ptr = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9]{12}");
        Matcher matcher = ptr.matcher(name.trim());

        return matcher.matches();
    }

    public enum Degree {
        BACHELOR("бакалавариат"),
        MASTER("магистратура"),
        PHILOSOPHY("аспирантура"),
        SPECIALTY("специалитет");

        private String fullName;

        Degree(String fullName) {
            this.fullName = fullName;
        }

        public String getFullName() {
            return fullName;
        }
    }
}
