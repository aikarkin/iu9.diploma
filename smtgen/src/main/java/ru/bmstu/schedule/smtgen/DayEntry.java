package ru.bmstu.schedule.smtgen;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DayEntry {

    private static final int MAX_ITEMS_PER_DAY = 7;

    private LessonItem[] items;
    private DayOfWeek dayOfWeek;

    public DayEntry() {
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

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DayEntry that = (DayEntry) o;
        return Arrays.equals(items, that.items) &&
                dayOfWeek == that.dayOfWeek;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(dayOfWeek);
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
                dayOfWeek.getAlias(),
                String.join("\n", itemsStrs)
        );
    }

}
