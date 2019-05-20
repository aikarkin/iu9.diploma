package ru.bmstu.schedule.html.node;

import ru.bmstu.schedule.html.commons.LeafNode;

import java.util.Objects;


public class ScheduleItemParityNode extends LeafNode<ScheduleItemNode> {
    private String subject;
    private String classType;
    private String classroom;
    private String lecturer;
    private DayParity dayParity;

    public ScheduleItemParityNode(String subject, String classType, String classroom, String lecturer) {
        this.subject = subject;
        this.classType = classType;
        this.classroom = classroom;
        this.lecturer = lecturer;
    }

    public String getSubject() {
        return subject;
    }

    public String getClassType() {
        return classType;
    }

    public String getClassroom() {
        return classroom;
    }

    public String getLecturer() {
        return lecturer;
    }

    public DayParity getDayParity() {
        return dayParity;
    }

    public void setDayParity(DayParity dayParity) {
        this.dayParity = dayParity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleItemParityNode that = (ScheduleItemParityNode) o;
        return Objects.equals(subject, that.subject) &&
                Objects.equals(classType, that.classType) &&
                Objects.equals(classroom, that.classroom) &&
                Objects.equals(lecturer, that.lecturer) &&
                dayParity == that.dayParity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, classType, classroom, lecturer, dayParity);
    }

    @Override
    public String toString() {
        return "ScheduleItemParityNode{" +
                "subject='" + subject + '\'' +
                ", classType='" + classType + '\'' +
                ", classroom='" + classroom + '\'' +
                ", lecturer='" + lecturer + '\'' +
                ", dayParity=" + dayParity +
                '}';
    }

    public enum DayParity {
        NUMERATOR,
        DENUMERATOR,
        ANY;

        @Override
        public String toString() {
            if(this == NUMERATOR)
                return "ЧС";
            else if(this == DENUMERATOR)
                return "ЗН";

            return "ЧС/ЗН";
        }
    }
}
