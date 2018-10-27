package ru.bmstu.schedule.html.selector;

public enum  CourseSelector implements QuerySelector {
    courseName("> span:eq(0)"),
    groups("> *:not(:eq(0))")
    ;

    private String query;

    CourseSelector(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
