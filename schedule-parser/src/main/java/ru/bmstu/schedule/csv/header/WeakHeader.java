package ru.bmstu.schedule.csv.header;

public enum WeakHeader implements CSVHeader {
    abbreviation,
    title;

    @Override
    public String getHeader() {
        return name();
    }
}
