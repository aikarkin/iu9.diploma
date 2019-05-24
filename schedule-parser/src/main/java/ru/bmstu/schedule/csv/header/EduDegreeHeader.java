package ru.bmstu.schedule.csv.header;

public enum EduDegreeHeader implements CSVHeader {
    name,
    numberOfStudyYears;

    @Override
    public String getHeader() {
        return name();
    }
}
