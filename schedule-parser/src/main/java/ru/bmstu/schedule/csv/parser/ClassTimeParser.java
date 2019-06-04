package ru.bmstu.schedule.csv.parser;

import ru.bmstu.schedule.csv.RecordHolder;
import ru.bmstu.schedule.csv.header.ClassTimeHeader;
import ru.bmstu.schedule.entity.ClassTime;

import java.text.ParseException;

public class ClassTimeParser implements EntryParser<ClassTime, ClassTimeHeader> {
    @Override
    public ClassTime parse(RecordHolder<ClassTimeHeader> rec) {
        try {
            ClassTime ct = new ClassTime();
            rec.fillTime(ct::setStartsAt, ClassTimeHeader.startsAt);
            rec.fillTime(ct::setEndsAt, ClassTimeHeader.endsAt);
            int noOfClass = (int) rec.record().getRecordNumber();
            ct.setNoOfClass(noOfClass);
            return ct;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
