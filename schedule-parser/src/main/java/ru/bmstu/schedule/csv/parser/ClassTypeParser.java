package ru.bmstu.schedule.csv.parser;

import ru.bmstu.schedule.csv.RecordHolder;
import ru.bmstu.schedule.csv.header.ClassTypeHeader;
import ru.bmstu.schedule.entity.ClassType;

public class ClassTypeParser implements EntryParser<ClassType, ClassTypeHeader> {
    @Override
    public ClassType parse(RecordHolder<ClassTypeHeader> rec) {
        ClassType ct = new ClassType();
        rec.fillString(ct::setName, ClassTypeHeader.title);

        return ct;
    }
}
