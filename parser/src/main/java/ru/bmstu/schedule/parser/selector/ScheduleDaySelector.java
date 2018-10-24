package ru.bmstu.schedule.parser.selector;

public enum ScheduleDaySelector implements QuerySelector {
    dayOfWeak("> tr:eq(0) > td > strong"),
    scheduleItems("> tr"),
    time("> td:eq(0)"),
    paritiesItems("> td")
    ;

    private String query;

    ScheduleDaySelector(String query) {
        this.query = query;
    }

    @Override
    public String getQuery() {
        return this.query;
    }
}
