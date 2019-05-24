package ru.bmstu.schedule.html.node;

import org.jsoup.select.Elements;
import ru.bmstu.schedule.html.ScheduleItemDeserializer;
import ru.bmstu.schedule.html.commons.RootNode;

import java.util.Objects;

public class ScheduleDayNode extends RootNode<ScheduleItemNode> {
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    private DayOfWeek dayOfWeek;

    public ScheduleDayNode(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public void parseChildren(Elements elements) {
        super.parseChildren(ScheduleItemDeserializer.class, elements);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleDayNode that = (ScheduleDayNode) o;
        return dayOfWeek == that.dayOfWeek;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek);
    }

    @Override
    public String toString() {
        return "ScheduleDayNode{" +
                "dayOfWeak=" + dayOfWeek +
                '}';
    }

    public enum DayOfWeek {
        MON("Понедельник"),
        TUES("Вторник"),
        WED("Среда"),
        THU("Четверг"),
        FRI("Пятница"),
        SAT("Суббота"),
        SUN("Воскресенье")
        ;

        public String getWeekName() {
            return weakValue;
        }

        private String weakValue;


        DayOfWeek(String weakValue) {
            this.weakValue = weakValue;
        }

        @Override
        public String toString() {
            return weakValue;
        }
    }
}
