package ru.bmstu.schedule.csv.header;

public enum ClassTypeHeader implements CSVHeader {
    abbreviation,
    title;

    @Override
    public String getHeader() {
        return name();
    }
}
