package ru.bmstu.schedule.csv;

import org.apache.commons.csv.CSVRecord;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecordHolder<E extends Enum<?>> {
    private CSVRecord rec;

    public RecordHolder(CSVRecord rec) {
        this.rec = rec;
    }

    private static <E extends Enum<?>> List<String> parseList(CSVRecord rec, E prop) {
        String val = rec.get(prop);
        return Stream.of(val.split(";")).map(String::trim).collect(Collectors.toList());
    }

    private static <E extends Enum<?>> Integer parseInt(CSVRecord rec, E prop) {
        String val = rec.get(prop);
        return Integer.parseInt(val);
    }

    private static <E extends Enum<?>> Double parseDouble(CSVRecord rec, E prop) {
        String val = rec.get(prop);
        return Double.parseDouble(val);
    }

    private static <E extends Enum<?>> Time parseTime(CSVRecord rec, E prop) throws ParseException {
        String val = rec.get(prop);
        DateFormat df = new SimpleDateFormat("hh:mm:ss");
        long ms = df.parse(val).getTime();

        return new Time(ms);
    }

    public CSVRecord record() {
        return rec;
    }

    public String get(E prop) {
        return this.rec.get(prop).trim();
    }

    public Optional<Integer> getInt(E prop) {
        Integer intVal = null;
        try {
            intVal = parseInt(record(), prop);
        } catch (NumberFormatException ignored) { }

        return Optional.ofNullable(intVal);
    }

    public void fillString(Consumer<String> setter, E propName) {
        setter.accept(this.get(propName));
    }

    public void fillInt(Consumer<Integer> setter, E propName) {
        setter.accept(parseInt(rec, propName));
    }

    public void fillDouble(Consumer<Double> setter, E propName) {
        setter.accept(parseDouble(rec, propName));
    }

    public void fillList(Consumer<List> setter, E propName) {
        setter.accept(parseList(rec, propName));
    }

    public void fillTime(Consumer<Time> setter, E propName) throws ParseException {
        setter.accept(parseTime(rec, propName));
    }
}
