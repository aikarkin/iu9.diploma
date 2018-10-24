package ru.bmstu.schedule.parser.selector;

public enum FacultySelector implements QuerySelector {
    cipher("> h4"),
    name("> p"),
    departments("> .panel-body > div"),
    ;

    private String query;

    FacultySelector(String query) {
        this.query = query;
    }

    @Override
    public String getQuery() {
        return query;
    }
}
