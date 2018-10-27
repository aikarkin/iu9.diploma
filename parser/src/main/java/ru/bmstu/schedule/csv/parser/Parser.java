package ru.bmstu.schedule.csv.parser;

import ru.bmstu.schedule.csv.RecordHolder;

// T entity type
public interface Parser<T> {
    T parse(RecordHolder rec);
}
