package ru.bmstu.schedule.csv.parser;

import ru.bmstu.schedule.entity.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ParserFactory {
    
    @SuppressWarnings("unchecked")
    public static<E> Parser<E, ? extends Enum<?>> parserFor(Class<E> entityClazz) throws NotImplementedException {
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
        throw new NotImplementedException();
    }
}
