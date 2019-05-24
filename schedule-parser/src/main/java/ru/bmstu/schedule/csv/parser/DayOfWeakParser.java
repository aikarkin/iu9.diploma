package ru.bmstu.schedule.csv.parser;

import ru.bmstu.schedule.csv.RecordHolder;
import ru.bmstu.schedule.csv.header.WeakHeader;
import ru.bmstu.schedule.entity.DayOfWeek;

public class DayOfWeakParser implements Parser<DayOfWeek, WeakHeader> {
    @Override
    public DayOfWeek parse(RecordHolder<WeakHeader> rec) {
        DayOfWeek weak = new DayOfWeek();
        rec.fillString(weak::setShortName, WeakHeader.abbreviation);
        rec.fillString(weak::setName, WeakHeader.title);

        return weak;
    }
}
