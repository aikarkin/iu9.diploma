package ru.bmstu.schedule.csv.parser;

import ru.bmstu.schedule.csv.header.CSVHeader;
import ru.bmstu.schedule.entity.*;

public class ParserFactory {
    
    @SuppressWarnings("unchecked")
    public static<E> Parser<E, ? extends CSVHeader> parserFor(Class<E> entityClazz) throws IllegalStateException {
        if(entityClazz == Lecturer.class) {
            return (Parser<E, ?>) new LecturerParser();
        } else if(entityClazz == DayOfWeak.class) {
            return (Parser<E, ?>) new DayOfWeakParser();
        } else if(entityClazz == ClassType.class) {
            return (Parser<E, ?>) new ClassTypeParser();
        } else if(entityClazz == ClassTime.class) {
            return (Parser<E, ?>) new ClassTimeParser();
        } else if(entityClazz == EduDegree.class) {
            return (Parser<E, ?>) new EduDegreeParser();
        } else if(entityClazz == Department.class) {
            return (Parser<E, ?>) new DepartmentParser();
        } else if(entityClazz == Specialization.class) {
            return (Parser<E, ?>) new SpecializationParser();
        }
        throw new IllegalStateException("No parser exists for specified entity");
    }
}
