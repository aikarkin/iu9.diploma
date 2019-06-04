package ru.bmstu.schedule.csv.header;

public enum DepartmentHeader implements CSVHeader {
    code,
    title;

    @Override
    public String getHeader() {
        return name();
    }
}
