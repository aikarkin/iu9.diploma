package ru.bmstu.schedule.csv.header;

public enum SpecializationHeader implements CSVHeader {
    specializationCode("Код специализации"),
    specialityName("Наименование специальности"),
    specializationName("Специализация"),
    degree("Квалификация"),
    degreeStudyYears("Срок");

    private String header;

    SpecializationHeader(String header) {
        this.header = header;
    }

    @Override
    public String getHeader() {
        return header;
    }
}
