package ru.bmstu.schedule.csv;

import org.apache.commons.csv.CSVRecord;
import ru.bmstu.schedule.csv.header.CSVHeader;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecordHolder<E extends CSVHeader> {

    private CSVRecord rec;

    public RecordHolder(CSVRecord rec) {
        this.rec = rec;
    }

    public CSVRecord record() {
        return rec;
    }

    public String get(E prop) {
        return this.rec.get(prop.getHeader()).trim();
    }

    public Optional<Integer> getInt(E prop) {
        Integer intVal = null;
        try {
            intVal = parseInt(record(), prop);
        } catch (NumberFormatException ignored) {
        }

        return Optional.ofNullable(intVal);
    }

    public List<String> getList(E prop) {
        return parseList(rec, prop);
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

    public void fillSet(Consumer<Set> setter, E propName) {
        setter.accept(parseSet(rec, propName));
    }

    public void fillTime(Consumer<Time> setter, E propName) throws ParseException {
        setter.accept(parseTime(rec, propName));
    }

    private static <E extends CSVHeader> List<String> parseList(CSVRecord rec, E prop) {
        String val = rec.get(prop.getHeader());
        return Stream.of(val.split(";\\s+")).map(String::trim).collect(Collectors.toList());
    }

    private static <E extends CSVHeader> Set<String> parseSet(CSVRecord rec, E prop) {
        String val = rec.get(prop.getHeader());
        return Stream.of(val.split(";\\s+")).map(String::trim).collect(Collectors.toSet());
    }

    private static <E extends CSVHeader> Integer parseInt(CSVRecord rec, E prop) {
        String val = rec.get(prop.getHeader());
        return Integer.parseInt(val);
    }

    private static <E extends CSVHeader> Double parseDouble(CSVRecord rec, E prop) {
        String val = rec.get(prop.getHeader());
        return Double.parseDouble(val);
    }

    private static <E extends CSVHeader> Time parseTime(CSVRecord rec, E prop) throws ParseException {
        String val = rec.get(prop.getHeader());
        DateFormat df = new SimpleDateFormat("hh:mm:ss");
        long ms = df.parse(val).getTime();

        return Time.valueOf(val);
    }

}
