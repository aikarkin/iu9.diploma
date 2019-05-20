package ru.bmstu.schedule.html.node;

import org.jsoup.select.Elements;
import ru.bmstu.schedule.html.ScheduleItemDeserializer;
import ru.bmstu.schedule.html.commons.RootNode;

import java.util.Objects;

public class ScheduleDayNode extends RootNode<ScheduleItemNode> {
    public DayOfWeak getDayOfWeak() {
        return dayOfWeak;
    }

    private DayOfWeak dayOfWeak;

    public ScheduleDayNode(DayOfWeak dayOfWeak) {
        this.dayOfWeak = dayOfWeak;
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
        return dayOfWeak == that.dayOfWeak;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeak);
    }

    @Override
    public String toString() {
        return "ScheduleDayNode{" +
                "dayOfWeak=" + dayOfWeak +
                '}';
    }

    public enum DayOfWeak {
        MON("Понедельник"),
        TUES("Вторник"),
        WED("Среда"),
        THU("Четверг"),
        FRI("Пятница"),
        SAT("Суббота"),
        SUN("Воскресенье")
        ;

        public String getWeakName() {
            return weakValue;
        }

        private String weakValue;


        DayOfWeak(String weakValue) {
            this.weakValue = weakValue;
        }

        @Override
        public String toString() {
            return weakValue;
        }
    }
}
