package ru.bmstu.schedule.csv.parser;

import org.apache.commons.lang3.StringUtils;
import ru.bmstu.schedule.csv.RecordHolder;
import ru.bmstu.schedule.entity.Lecturer;
import ru.bmstu.schedule.csv.header.Header;

import java.util.Optional;


public class LecturerParser implements Parser<Lecturer> {

    @Override
    public Lecturer parse(RecordHolder rec) {
        Lecturer lec = new Lecturer();
        Optional<String[]> fnOpt = parseFullName(rec.get(Header.fullName));
        if(fnOpt.isPresent()) {
            String[] fnArr = fnOpt.get();
            lec.setLastName(fnArr[0]);
            lec.setFirstName(fnArr[1]);
            lec.setMiddleName(fnArr[2]);
        }
        if(StringUtils.isNotEmpty(rec.get(Header.eduDegree))) {
            rec.fillString(lec::setEduDegree, Header.eduDegree);
        }

        return lec;
    }

    private static Optional<String[]> parseFullName(String str) {
        if(str != null && str.length() > 0) {
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
