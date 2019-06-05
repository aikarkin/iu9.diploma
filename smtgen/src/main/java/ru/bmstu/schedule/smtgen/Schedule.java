package ru.bmstu.schedule.smtgen;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Schedule {

    private static final int MAX_DAYS_PER_WEAK = DayOfWeak.values().length;
    private ScheduleDay[] scheduleDays = new ScheduleDay[MAX_DAYS_PER_WEAK];

    public Schedule() {
    }

    public Schedule(ScheduleDay[] scheduleDays) {
        this.scheduleDays = scheduleDays;
    }

    public void setDay(int i, ScheduleDay scheduleDay) {
        if (i < 0 || i >= MAX_DAYS_PER_WEAK)
            throw new IllegalArgumentException("Invalid day index - day index should be positive and less then " + MAX_DAYS_PER_WEAK);

        this.scheduleDays[i] = scheduleDay;
    }

    @Override
    public String toString() {
        return Stream.of(scheduleDays)
                .map(day -> day == null ? "" : day.toString())
                .collect(Collectors.joining("\n\n"));
    }

}
