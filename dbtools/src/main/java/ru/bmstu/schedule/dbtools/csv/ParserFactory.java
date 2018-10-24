package ru.bmstu.schedule.dbtools.csv;

import org.apache.commons.lang3.StringUtils;
import ru.bmstu.schedule.dbtools.csv.property.*;
import ru.bmstu.schedule.entity.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


import java.text.ParseException;
import java.util.Optional;

public class ParserFactory {
    public static Parser<?> parserFor(Class<?> entityClazz) {
        if(entityClazz == Lecturer.class) {
            return rec -> {
                Lecturer lec = new Lecturer();
                Optional<String[]> fnOpt = parseFullName(rec.get(LecturerProperty.fullName));
                if(fnOpt.isPresent()) {
                    String[] fnArr = fnOpt.get();
                    lec.setLastName(fnArr[0]);
                    lec.setFirstName(fnArr[1]);
                    lec.setMiddleName(fnArr[2]);
                }
                if(StringUtils.isNotEmpty(rec.get(LecturerProperty.eduDegree))) {
                    rec.fillString(lec::setEduDegree, LecturerProperty.eduDegree);
                }

                return lec;
            };
        } else if(entityClazz == DayOfWeak.class) {
            return rec -> {
                DayOfWeak weak = new DayOfWeak();
                rec.fillString(weak::setShortName, WeakProperty.abbreviation);
                rec.fillString(weak::setName, WeakProperty.title);

                return weak;
            };
        } else if(entityClazz == ClassType.class) {
            return rec -> {
                ClassType ct = new ClassType();
                rec.fillString(ct::setName, ClassProperty.title);

                return ct;
            };
        } else if(entityClazz == ClassTime.class) {
            return rec -> {
                try {
                    ClassTime ct = new ClassTime();
                    rec.fillTime(ct::setStartsAt, ClassTimeProperty.startsAt);
                    rec.fillTime(ct::setEndsAt, ClassTimeProperty.endsAt);
                    int noOfClass = (int) rec.record().getRecordNumber();
                    ct.setNoOfClass(noOfClass);
                    return ct;
                } catch (ParseException e) {
                    e.printStackTrace();
                    return null;
                }
            };
        } else if(entityClazz == EduDegree.class) {
            return rec -> {
                EduDegree degree = new EduDegree();
                rec.fillString(degree::setName, EduDegreeProperty.name);
                rec.fillInt(degree::setMinNumberOfStudyYears, EduDegreeProperty.numberOfStudyYears);

                return degree;
            };
        } else if(entityClazz == Department.class) {
            return rec -> {
                Department department = new Department();
                rec.fillInt(department::setNumber, DepartmentProperty.departmentNumber);
                rec.fillString(department::setTitle, DepartmentProperty.title);

                return department;
            };
        } else if(entityClazz == Specialization.class) {
            return rec -> {
                Specialization spec = new Specialization();
                rec.fillString(spec::setCode, SpecProperty.code);
                rec.fillString(spec::setTitle, SpecProperty.title);

                return spec;
            };
        }
        throw new NotImplementedException();
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
