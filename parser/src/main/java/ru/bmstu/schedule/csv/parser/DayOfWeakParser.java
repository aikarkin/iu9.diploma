package ru.bmstu.schedule.csv.parser;

import ru.bmstu.schedule.csv.RecordHolder;
import ru.bmstu.schedule.csv.header.WeakHeader;
import ru.bmstu.schedule.entity.DayOfWeak;

public class DayOfWeakParser implements Parser<DayOfWeak, WeakHeader> {
    @Override
    public DayOfWeak parse(RecordHolder<WeakHeader> rec) {
        DayOfWeak weak = new DayOfWeak();
        rec.fillString(weak::setShortName, WeakHeader.abbreviation);
        rec.fillString(weak::setName, WeakHeader.title);

        return weak;
    }
}
