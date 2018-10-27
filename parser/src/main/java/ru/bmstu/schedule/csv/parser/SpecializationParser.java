package ru.bmstu.schedule.csv.parser;

import ru.bmstu.schedule.csv.RecordHolder;
import ru.bmstu.schedule.csv.property.SpecProperty;
import ru.bmstu.schedule.entity.Specialization;

public class SpecializationParser implements Parser<Specialization> {
    @Override
    public Specialization parse(RecordHolder rec) {
        Specialization spec = new Specialization();
        rec.fillString(spec::setCode, SpecProperty.code);
        rec.fillString(spec::setTitle, SpecProperty.title);

        return spec;
    }
}
