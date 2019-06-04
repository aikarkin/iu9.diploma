package ru.bmstu.schedule.csv.parser;

import org.apache.commons.lang3.StringUtils;
import ru.bmstu.schedule.csv.LecturerEntry;
import ru.bmstu.schedule.csv.RecordHolder;
import ru.bmstu.schedule.csv.header.LecturerHeader;

import java.util.Optional;


public class LecturerParser implements EntryParser<LecturerEntry, LecturerHeader> {

    @Override
    public LecturerEntry parse(RecordHolder<LecturerHeader> rec) {
        LecturerEntry lec = new LecturerEntry();
        Optional<String[]> fnOpt = parseFullName(rec.get(LecturerHeader.fullName));
        if (fnOpt.isPresent()) {
            String[] fnArr = fnOpt.get();
            lec.setLastName(fnArr[0]);
            lec.setFirstName(fnArr[1]);
            lec.setMiddleName(fnArr[2]);
        }
        if (StringUtils.isNotEmpty(rec.get(LecturerHeader.eduDegree))) {
            rec.fillString(lec::setEduDegree, LecturerHeader.eduDegree);
        }

        rec.fillSet(lec::setSpecializationsCodes, LecturerHeader.specialities);
        rec.fillSet(lec::setSubjectsNames, LecturerHeader.subjects);

        return lec;
    }

    private static Optional<String[]> parseFullName(String str) {
        if (StringUtils.isNotEmpty(str)) {
            String[] space = str.split(" ");
            if (space.length >= 3) {
                for (int i = 0; i < space.length; i++) {
                    space[i] = space[i].trim();
                }
                return Optional.of(space);
            }
        }

        return Optional.empty();
    }

}
