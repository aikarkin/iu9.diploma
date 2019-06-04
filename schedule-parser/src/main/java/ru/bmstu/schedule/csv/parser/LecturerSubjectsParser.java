package ru.bmstu.schedule.csv.parser;

import ru.bmstu.schedule.csv.LecturerSubjectEntry;
import ru.bmstu.schedule.csv.LecturerSubjectEntry.ClassKind;
import ru.bmstu.schedule.csv.RecordHolder;
import ru.bmstu.schedule.csv.header.LecturerSubjectsHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LecturerSubjectsParser implements EntryParser<LecturerSubjectEntry, LecturerSubjectsHeader> {

    private static final String SUBJ_RE = "([\\p{L}\\s]+)(\\((\\p{L}+)\\.\\))?";
    private static final String DEPT_RE = "\\p{Lu}+(\\d+)?";
    private static final String LEC_RE = "\\p{Lu}\\p{L}+\\s\\p{Lu}\\.\\s\\p{Lu}\\.";

    @Override
    public LecturerSubjectEntry parse(RecordHolder<LecturerSubjectsHeader> rec) {
        LecturerSubjectEntry entry = new LecturerSubjectEntry();
        List<String> subjectsStrings = rec.getList(LecturerSubjectsHeader.subjects);
        List<Map.Entry<ClassKind, String>> subjects = new ArrayList<>();
        Pattern subjPtr = Pattern.compile(SUBJ_RE);

        for (String subject : subjectsStrings) {
            Matcher subjMatcher = subjPtr.matcher(subject);
            if (subjMatcher.matches() && subjMatcher.groupCount() > 1) {
                if (subjMatcher.group(1) == null) {
                    continue;
                }

                String subj = subjMatcher.group(1).trim();
                ClassKind classKind = ClassKind.kindByShortName(subjMatcher.group(3));
                Map.Entry<ClassKind, String> subjEntry = new HashMap.SimpleEntry<>(classKind, subj);
                subjects.add(subjEntry);
            }
        }

        entry.setSubjectsOfKind(subjects);
        String deptStr = rec.get(LecturerSubjectsHeader.department);
        String lecStr = rec.get(LecturerSubjectsHeader.lecturer);

        if (deptStr == null || !deptStr.matches(DEPT_RE)) {
            System.out.println("[error] Invalid department name: " + deptStr);
        } else {
            entry.setDepartment(deptStr);
        }

        if (lecStr == null || !lecStr.matches(LEC_RE)) {
            System.out.println("[error] Invalid lecturer initials: " + lecStr);
        } else {
            entry.setLecturer(lecStr);
        }

        return entry;
    }

}
