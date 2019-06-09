package ru.bmstu.schedule.csv.header;

public enum  ClasroomHeader implements CSVHeader {
    roomNo;

    @Override
    public String getHeader() {
        return name();
    }
}
