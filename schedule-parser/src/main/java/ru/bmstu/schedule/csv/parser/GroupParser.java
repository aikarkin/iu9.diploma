package ru.bmstu.schedule.csv.parser;

import ru.bmstu.schedule.csv.GroupEntry;
import ru.bmstu.schedule.csv.RecordHolder;
import ru.bmstu.schedule.csv.header.GroupHeader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupParser implements EntryParser<GroupEntry, GroupHeader> {

    private static final Pattern GROUP_CODE_PTR = Pattern.compile("(\\p{Lu}+)(\\d+)-(\\d+)(\\p{Lu})?");
    private static final Pattern SPEC_CODE_PTR = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{2})_(\\d+)");


    @Override
    public GroupEntry parse(RecordHolder<GroupHeader> rec) {
        GroupEntry entry = new GroupEntry();

        String groupCode = rec.get(GroupHeader.group);
        String specializationCode = rec.get(GroupHeader.specCode);

        Matcher grCipherMatcher = GROUP_CODE_PTR.matcher(groupCode);
        Matcher specCipherMatcher = SPEC_CODE_PTR.matcher(specializationCode);

        if (grCipherMatcher.matches() && grCipherMatcher.groupCount() >= 3) {
            String factCipher = grCipherMatcher.group(1);
            int deptNumber = Integer.valueOf(grCipherMatcher.group(2));
            int grCode = Integer.valueOf(grCipherMatcher.group(3));
            int grNumber = grCode % 10;
            int grTerm = grCode / 10;

            char grLetter = ' ';
            String degreeName;

            if (grCipherMatcher.group(4) != null) {
                grLetter = grCipherMatcher.group(4).charAt(0);
            }

            switch (grLetter) {
                case 'М':
                    degreeName = "магистратура";
                    break;
                case 'А':
                    degreeName = "аспирантура";
                    break;
                default:
                    degreeName = "бакалавариат";
                    break;
            }

            entry.setDegreeName(degreeName);
            entry.setDepartmentNumber(deptNumber);
            entry.setGroupNumber(grNumber);
            entry.setTermNumber(grTerm);
            entry.setFacultyCipher(factCipher);
        }

        if (specCipherMatcher.matches() && specCipherMatcher.groupCount() >= 2) {
            entry.setSpecialityCode(specCipherMatcher.group(1));
            entry.setSpecializationNumber(Integer.valueOf(specCipherMatcher.group(2)));
        }

        return entry;
    }

}
