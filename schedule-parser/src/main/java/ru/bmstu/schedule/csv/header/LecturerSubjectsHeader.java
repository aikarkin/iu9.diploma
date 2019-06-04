package ru.bmstu.schedule.csv.header;

public enum LecturerSubjectsHeader implements CSVHeader {
    lecturer,
    subjects,
    department;

    @Override
    public String getHeader() {
        return name();
    }

}
