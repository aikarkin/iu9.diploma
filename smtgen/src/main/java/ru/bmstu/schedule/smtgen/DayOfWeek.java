package ru.bmstu.schedule.smtgen;

public enum DayOfWeek {
    mon("ПН"),
    tue("ВТ"),
    wed("СР"),
    thu("ЧТ"),
    fri("ПТ"),
    sat("СБ");

    private String alias;

    DayOfWeek(String name) {
        this.alias = name;
    }

    public String getAlias() {
        return alias;
    }
}
