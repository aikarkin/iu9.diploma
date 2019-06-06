package ru.bmstu.schedule.smtgen;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Schedule {

    private static final int MAX_DAYS_PER_WEAK = DayOfWeek.values().length;
    private DayEntry[] dayEntries = new DayEntry[MAX_DAYS_PER_WEAK];

    public Schedule() {
    }

    public Schedule(DayEntry[] dayEntries) {
        this.dayEntries = dayEntries;
    }

    public void setDay(int i, DayEntry dayEntry) {
        if (i < 0 || i >= MAX_DAYS_PER_WEAK)
            throw new IllegalArgumentException("Invalid day index - day index should be positive and less then " + MAX_DAYS_PER_WEAK);

        this.dayEntries[i] = dayEntry;
    }

    public DayEntry[] getDayEntries() {
        return dayEntries;
    }

    @Override
    public String toString() {
        return Stream.of(dayEntries)
                .map(day -> day == null ? "" : day.toString())
                .collect(Collectors.joining("\n\n"));
    }

}
