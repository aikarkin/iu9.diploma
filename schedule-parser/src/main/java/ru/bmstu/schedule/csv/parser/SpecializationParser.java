package ru.bmstu.schedule.csv.parser;

import ru.bmstu.schedule.csv.RecordHolder;
import ru.bmstu.schedule.csv.header.SpecHeader;
import ru.bmstu.schedule.entity.Specialization;

public class SpecializationParser implements Parser<Specialization, SpecHeader> {
    @Override
    public Specialization parse(RecordHolder<SpecHeader> rec) {
        Specialization spec = new Specialization();
        rec.fillString(spec::setCode, SpecHeader.code);
        rec.fillString(spec::setTitle, SpecHeader.title);

        return spec;
    }
}
