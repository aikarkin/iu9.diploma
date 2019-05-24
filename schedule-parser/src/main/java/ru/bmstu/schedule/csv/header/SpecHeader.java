package ru.bmstu.schedule.csv.header;

public enum SpecHeader implements CSVHeader {
    code,
    title,
    degree;

    @Override
    public String getHeader() {
        return name();
    }
}
