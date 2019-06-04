package ru.bmstu.schedule.csv.parser;

import ru.bmstu.schedule.csv.DepartmentEntry;
import ru.bmstu.schedule.csv.RecordHolder;
import ru.bmstu.schedule.csv.header.DepartmentHeader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DepartmentParser implements EntryParser<DepartmentEntry, DepartmentHeader> {

    private static final Pattern DEPT_CIPHER_PTR = Pattern.compile("(\\p{Lu}+)(\\d+)?");

    @Override
    public DepartmentEntry parse(RecordHolder<DepartmentHeader> rec) {
        DepartmentEntry entry = new DepartmentEntry();

        rec.fillString(entry::setDepartmentTitle, DepartmentHeader.title);

        Matcher codeMatcher = DEPT_CIPHER_PTR.matcher(rec.get(DepartmentHeader.code));
        if (codeMatcher.matches() && codeMatcher.groupCount() >= 1) {
            String factCipher = codeMatcher.group(1);
            int deptNumber = 0;
            if (codeMatcher.group(2) != null) {
                deptNumber = Integer.valueOf(codeMatcher.group(2));
            }
            entry.setDepartmentNumber(deptNumber);
            entry.setFacultyCipher(factCipher);
        }

        return entry;
    }

}
