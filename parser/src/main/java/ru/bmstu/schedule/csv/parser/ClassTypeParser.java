package ru.bmstu.schedule.csv.parser;

import ru.bmstu.schedule.csv.RecordHolder;
import ru.bmstu.schedule.csv.property.ClassProperty;
import ru.bmstu.schedule.entity.ClassType;

public class ClassTypeParser implements Parser<ClassType> {
    @Override
    public ClassType parse(RecordHolder rec) {
        ClassType ct = new ClassType();
        rec.fillString(ct::setName, ClassProperty.title);

        return ct;
    }
}
