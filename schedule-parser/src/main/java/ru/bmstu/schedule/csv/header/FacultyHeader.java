package ru.bmstu.schedule.csv.header;

public enum FacultyHeader implements CSVHeader {
    code,
    title;

    @Override
    public String getHeader() {
        return name();
    }

}
