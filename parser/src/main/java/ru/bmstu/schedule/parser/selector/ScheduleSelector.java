package ru.bmstu.schedule.parser.selector;

public enum ScheduleSelector implements QuerySelector {
    title(".container > .row > div:eq(0) > .page-header > h1"),
    noOfWeak(".container > .row > div:eq(0) > .page-header > h4 i"),
    days(".container > .row > .hidden-xs > table > tbody"),
    ;

    ScheduleSelector(String query) { this.query = query; }

    private String query;

    public String getQuery() {
        return this.query;
    }
}
