package ru.bmstu.schedule.csv.parser;

import ru.bmstu.schedule.csv.RecordHolder;
import ru.bmstu.schedule.csv.header.ClasroomHeader;
import ru.bmstu.schedule.entity.Classroom;

public class ClassroomParser implements EntryParser<Classroom, ClasroomHeader> {

    @Override
    public Classroom parse(RecordHolder<ClasroomHeader> rec) {
        Classroom classroom = new Classroom();
        rec.fillString(classroom::setRoomNumber, ClasroomHeader.roomNo);
        return classroom;
    }

}
