package ru.bmstu.schedule.csv.parser;

import ru.bmstu.schedule.csv.RecordHolder;
import ru.bmstu.schedule.csv.SpecializationEntry;
import ru.bmstu.schedule.csv.header.SpecializationHeader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.round;

public class SpecializationParser implements EntryParser<SpecializationEntry, SpecializationHeader> {

    private static final Pattern SPEC_CODE_PTR = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{2})_(\\d+)");
    private static final Pattern YEARS_PTR = Pattern.compile("(\\d+)г\\.(\\s(\\d+)м\\.)?");

    @Override
    public SpecializationEntry parse(RecordHolder<SpecializationHeader> rec) {
        SpecializationEntry entry = new SpecializationEntry();

        String specCode = rec.get(SpecializationHeader.specializationCode);
        Matcher codeMatcher = SPEC_CODE_PTR.matcher(specCode);
        if (codeMatcher.matches() && codeMatcher.groupCount() >= 2) {
            String specialityCode = codeMatcher.group(1);
            String specializationNumber = codeMatcher.group(2);
            entry.setSpecialityCode(specialityCode);
            entry.setNumberInSpeciality(Integer.valueOf(specializationNumber));
        }
        rec.fillString(entry::setSpecialityName, SpecializationHeader.specialityName);
        rec.fillString(entry::setSpecializationName, SpecializationHeader.specializationName);
        rec.fillString(entry::setDegreeName, SpecializationHeader.degree);
        Matcher yearsMatcher = YEARS_PTR.matcher(rec.get(SpecializationHeader.degreeStudyYears));

        if (yearsMatcher.matches() && yearsMatcher.groupCount() > 2) {
            int years = Integer.valueOf(yearsMatcher.group(1));
            int month = yearsMatcher.group(3) == null ? 0 : Integer.valueOf(yearsMatcher.group(3));
            entry.setDegreeStudyYears(years + (int) round((double) month / 12.0));
        }

        return entry;
    }

}
