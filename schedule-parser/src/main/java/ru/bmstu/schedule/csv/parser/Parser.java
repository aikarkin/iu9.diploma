package ru.bmstu.schedule.csv.parser;

import ru.bmstu.schedule.csv.RecordHolder;

// T entity type
public interface Parser<T, E extends Enum<?>> {
    T parse(RecordHolder<E> rec);
}
