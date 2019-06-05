package ru.bmstu.schedule.smtgen;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScheduleDay {

    private static final int MAX_ITEMS_PER_DAY = 7;

    private LessonItem[] items;
    private DayOfWeak dayOfWeak;

    public ScheduleDay() {
        items = new LessonItem[MAX_ITEMS_PER_DAY];
    }

    public LessonItem[] getItems() {
        return items;
    }

    public void setItems(LessonItem[] items) {
        this.items = items;
    }

    public void setItem(LessonItem item) {
        if (item.getIndex() >= MAX_ITEMS_PER_DAY)
            throw new IllegalArgumentException("Invalid index of lesson item - max number of items per day is " + MAX_ITEMS_PER_DAY);

        this.items[item.getIndex()] = item;
    }

    public void setItem(int index, Lesson lesson) {
        this.items[index] = new SingleLessonItem(index, lesson);
    }

    public void setItem(int index, Lesson numerator, Lesson denominator) {
        this.items[index] = new PairLessonItem(index, numerator, denominator);
    }

    public DayOfWeak getDayOfWeak() {
        return dayOfWeak;
    }

    public void setDayOfWeak(DayOfWeak dayOfWeak) {
        this.dayOfWeak = dayOfWeak;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleDay that = (ScheduleDay) o;
        return Arrays.equals(items, that.items) &&
                dayOfWeak == that.dayOfWeak;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(dayOfWeak);
        result = 31 * result + Arrays.hashCode(items);
        return result;
    }

    @Override
    public String toString() {
        List<String> itemsStrs = Stream.of(items)
                .map(item -> item == null ? "[ --- ]" : item.toString() )
                .collect(Collectors.toList());

        return String.format(
                "'%s':%n%s",
                dayOfWeak.name().toUpperCase(),
                String.join("\n", itemsStrs)
        );
    }

}
