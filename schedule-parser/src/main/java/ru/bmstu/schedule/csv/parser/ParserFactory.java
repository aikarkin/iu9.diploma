package ru.bmstu.schedule.csv.parser;

import ru.bmstu.schedule.csv.LecturerEntry;
import ru.bmstu.schedule.csv.header.CSVHeader;
import ru.bmstu.schedule.entity.*;

public class ParserFactory {
    
    @SuppressWarnings("unchecked")
    public static<E> Parser<E, ? extends CSVHeader> parserFor(Class<E> parsedEntryClass) throws IllegalStateException {
        if(parsedEntryClass == LecturerEntry.class) {
            return (Parser<E, ?>) new LecturerParser();
        } else if(parsedEntryClass == DayOfWeek.class) {
            return (Parser<E, ?>) new DayOfWeakParser();
        } else if(parsedEntryClass == ClassType.class) {
            return (Parser<E, ?>) new ClassTypeParser();
        } else if(parsedEntryClass == ClassTime.class) {
            return (Parser<E, ?>) new ClassTimeParser();
        } else if(parsedEntryClass == EduDegree.class) {
            return (Parser<E, ?>) new EduDegreeParser();
        } else if(parsedEntryClass == Department.class) {
            return (Parser<E, ?>) new DepartmentParser();
        } else if(parsedEntryClass == Specialization.class) {
            return (Parser<E, ?>) new SpecializationParser();
        }
        throw new IllegalStateException("No parser exists for specified entity");
    }
}
