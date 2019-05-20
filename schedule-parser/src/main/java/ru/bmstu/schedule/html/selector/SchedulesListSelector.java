package ru.bmstu.schedule.html.selector;

public enum SchedulesListSelector implements QuerySelector {
    faculty(".container > .list-group > a.list-group-item")
    ;

    private String query;

    SchedulesListSelector(String query) {
        this.query = query;
    }

    @Override
    public String getQuery() {
        return query;
    }
}
