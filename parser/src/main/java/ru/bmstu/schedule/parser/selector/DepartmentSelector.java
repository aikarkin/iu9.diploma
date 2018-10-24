package ru.bmstu.schedule.parser.selector;

public enum DepartmentSelector implements QuerySelector {
    name("> a > h4"),
    courses("> div > .btn-group")
    ;

    private String query;

    DepartmentSelector(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
