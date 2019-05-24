package ru.bmstu.schedule.csv.parser;

import ru.bmstu.schedule.csv.RecordHolder;
import ru.bmstu.schedule.csv.header.CSVHeader;

// T entity type
public interface Parser<T, E extends CSVHeader> {
    T parse(RecordHolder<E> rec);
}
