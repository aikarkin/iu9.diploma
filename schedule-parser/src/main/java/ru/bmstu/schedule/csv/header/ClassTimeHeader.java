package ru.bmstu.schedule.csv.header;

public enum ClassTimeHeader implements CSVHeader {
    startsAt,
    endsAt;

    @Override
    public String getHeader() {
        return name();
    }
}
