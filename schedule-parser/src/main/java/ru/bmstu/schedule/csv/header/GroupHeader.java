package ru.bmstu.schedule.csv.header;

public enum GroupHeader implements CSVHeader {
    group,
    specCode;

    @Override
    public String getHeader() {
        return name();
    }
}
