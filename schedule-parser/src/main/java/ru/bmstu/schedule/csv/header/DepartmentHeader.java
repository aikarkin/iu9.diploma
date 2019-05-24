package ru.bmstu.schedule.csv.header;

public enum DepartmentHeader implements CSVHeader {
    faculty,
    departmentNumber,
    cipher,
    title;

    @Override
    public String getHeader() {
        return name();
    }
}
