package ru.bmstu.schedule.csv.parser;

import ru.bmstu.schedule.csv.RecordHolder;
import ru.bmstu.schedule.csv.header.FacultyHeader;
import ru.bmstu.schedule.entity.Faculty;

public class FacultyParser implements EntryParser<Faculty, FacultyHeader> {

    @Override
    public Faculty parse(RecordHolder<FacultyHeader> rec) {
        Faculty faculty = new Faculty();

        rec.fillString(faculty::setCipher, FacultyHeader.code);
        rec.fillString(faculty::setTitle, FacultyHeader.title);

        return faculty;
    }

}
