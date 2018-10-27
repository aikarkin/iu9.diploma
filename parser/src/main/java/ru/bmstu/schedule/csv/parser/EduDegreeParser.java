package ru.bmstu.schedule.csv.parser;

import ru.bmstu.schedule.csv.RecordHolder;
import ru.bmstu.schedule.csv.property.EduDegreeProperty;
import ru.bmstu.schedule.entity.EduDegree;

public class EduDegreeParser implements Parser<EduDegree> {
    @Override
    public EduDegree parse(RecordHolder rec) {
        EduDegree degree = new EduDegree();
        rec.fillString(degree::setName, EduDegreeProperty.name);
        rec.fillInt(degree::setMinNumberOfStudyYears, EduDegreeProperty.numberOfStudyYears);

        return degree;
    }
}
