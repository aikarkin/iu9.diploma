package ru.bmstu.schedule.csv.header;

public enum SpecToDepHeader implements CSVHeader {
    specCode,
    departments;

    @Override
    public String getHeader() {
        return name();
    }
}
