package ru.bmstu.schedule.csv.parser;

import ru.bmstu.schedule.csv.RecordHolder;
import ru.bmstu.schedule.csv.property.WeakProperty;
import ru.bmstu.schedule.entity.DayOfWeak;

public class DayOfWeakParser implements Parser<DayOfWeak> {
    @Override
    public DayOfWeak parse(RecordHolder rec) {
        DayOfWeak weak = new DayOfWeak();
        rec.fillString(weak::setShortName, WeakProperty.abbreviation);
        rec.fillString(weak::setName, WeakProperty.title);

        return weak;
    }
}
