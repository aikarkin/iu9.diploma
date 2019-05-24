package ru.bmstu.schedule.csv.header;

public enum LecturerHeader implements CSVHeader {
    fullName,
    position,
    eduDegree,
    academicTitle,
    specialities,
    subjects;

    @Override
    public String getHeader() {
        return name();
    }
}
