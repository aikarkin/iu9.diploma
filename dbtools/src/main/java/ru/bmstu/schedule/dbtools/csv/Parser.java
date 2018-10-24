package ru.bmstu.schedule.dbtools.csv;

import org.apache.commons.csv.CSVRecord;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// T entity type
public interface Parser<T> {
    T parse(RecordHolder rec);
}
