package ru.bmstu.schedule.csv.parser;

import ru.bmstu.schedule.csv.LecturerEntry;
import ru.bmstu.schedule.csv.header.CSVHeader;
import ru.bmstu.schedule.entity.*;

public class ParserFactory {
    
    @SuppressWarnings("unchecked")
    public static <E> EntryParser<E, ? extends CSVHeader> parserFor(Class<E> parsedEntryClass) throws IllegalStateException {
        if(parsedEntryClass == LecturerEntry.class) {
            return (EntryParser<E, ?>) new LecturerParser();
        } else if(parsedEntryClass == DayOfWeek.class) {
            return (EntryParser<E, ?>) new DayOfWeakParser();
        } else if(parsedEntryClass == ClassType.class) {
            return (EntryParser<E, ?>) new ClassTypeParser();
        } else if(parsedEntryClass == ClassTime.class) {
            return (EntryParser<E, ?>) new ClassTimeParser();
        } else if(parsedEntryClass == Classroom.class) {
            return (EntryParser<E, ?>) new ClassroomParser();
        } else if (parsedEntryClass == Faculty.class) {
            return (EntryParser<E, ?>) new FacultyParser();
        } else if(parsedEntryClass == Department.class) {
            return (EntryParser<E, ?>) new DepartmentParser();
        } else if(parsedEntryClass == Specialization.class) {
            return (EntryParser<E, ?>) new SpecializationParser();
        }
        throw new IllegalStateException("No parser exists for specified entity");
    }
}
