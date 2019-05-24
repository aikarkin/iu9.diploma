package ru.bmstu.schedule.csv.header;

public enum CalendarHeader implements CSVHeader {
    subject("Наименование циклов, разделов, дисциплин"),
    department("Кафедра"),
    audHours("Ауд., час"),
    lectureHours("Лек., час"),
    seminarHours("Сем., час"),
    laboratoryHours("Лаб., час"),
    noOfTerm("Номер семестра"),
    certificationForm("Форма аттестации");

    String header;

    CalendarHeader(String header) {
        this.header = header;
    }

    @Override
    public String getHeader() {
        return header;
    }
}
