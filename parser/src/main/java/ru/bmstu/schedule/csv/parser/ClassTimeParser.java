package ru.bmstu.schedule.csv.parser;

import ru.bmstu.schedule.csv.RecordHolder;
import ru.bmstu.schedule.csv.property.ClassTimeProperty;
import ru.bmstu.schedule.entity.ClassTime;

import java.text.ParseException;

public class ClassTimeParser implements Parser<ClassTime> {
    @Override
    public ClassTime parse(RecordHolder rec) {
        try {
            ClassTime ct = new ClassTime();
            rec.fillTime(ct::setStartsAt, ClassTimeProperty.startsAt);
            rec.fillTime(ct::setEndsAt, ClassTimeProperty.endsAt);
            int noOfClass = (int) rec.record().getRecordNumber();
            ct.setNoOfClass(noOfClass);
            return ct;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
