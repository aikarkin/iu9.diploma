package ru.bmstu.schedule.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import ru.bmstu.schedule.csv.header.CalendarHeader;
import ru.bmstu.schedule.csv.parser.Parser;
import ru.bmstu.schedule.csv.parser.ParserFactory;
import ru.bmstu.schedule.dao.*;
import ru.bmstu.schedule.entity.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

public class CSVUtils {
    private static final String LABORATORY_TYPE_NAME = "лабораторная работа";
    private static final String LECTURER_TYPE_NAMAE = "лекция";
    private static final String SEMINAR_TYPE_NAME = "семинар";

    @SuppressWarnings("unchecked")
    public static <E, K extends Serializable> void
    fillFromCsv(HibernateDao<K, E> dao, String csvFile, BiConsumer<E, RecordHolder> entityConsumer)
        throws IOException, NotImplementedException {

        CSVParser parser = CSVFormat.EXCEL.withHeader().parse(new FileReader(csvFile));
        Parser<E, ?> entityParser = ParserFactory.parserFor(dao.getPersistentClass());

        for(CSVRecord rec : parser) {
            RecordHolder holder = new RecordHolder(rec);
            E parsed = (E) entityParser.parse(holder);
            entityConsumer.accept(parsed, holder);
            dao.create(parsed);
        }
    }


    public static <E, K extends Serializable> void
    fillFromCsv(HibernateDao<K, E> dao, String csvFile) throws IOException, NotImplementedException {
        fillFromCsv(dao, csvFile, (e, r) -> {});
    }

    public static void fillCalendar(StudyFlow studyFlow, SessionFactory sessionFactory, String csvFile) throws IOException {
        CSVParser parser = CSVFormat.EXCEL.withHeader().parse(new FileReader(csvFile));
        ClassTypeDao ctDao = new ClassTypeDao(sessionFactory);
        SubjectDao subjDao = new SubjectDao(sessionFactory);
        TermDao termDao = new TermDao(sessionFactory);
        StudyFlowDao flowDao = new StudyFlowDao(sessionFactory);

        Set<String> subjectsSet = new HashSet<>();

        boolean isOptionalSubject = false;
        String subjectName;
        int lectureHours = -1, seminarHours = -1, laboratoryHours = -1;
        int[] terms = new int[] {};

        Optional<ClassType> laboratoryCT = ctDao.findByTypeName(LABORATORY_TYPE_NAME),
                seminarCT = ctDao.findByTypeName(SEMINAR_TYPE_NAME),
                lectureCT = ctDao.findByTypeName(LECTURER_TYPE_NAMAE);

        for(CSVRecord rec : parser) {
            RecordHolder<CalendarHeader> holder = new RecordHolder<>(rec);

            subjectName = holder.get(CalendarHeader.subject);
            if(StringUtils.isNotEmpty(subjectName)) {
                // FIXME: hours of optional subjects is not added to database
                if (!isOptionalSubject || subjectName.startsWith("Дисциплина по выбору")) {
                    String noOfTermsVal = holder.get(CalendarHeader.noOfTerm);
                    String[] numbersOfTerms = noOfTermsVal.split("( - )|(, )");

                    if (numbersOfTerms.length > 1) {
                        if (noOfTermsVal.contains(",")) {
                            terms = new int[numbersOfTerms.length];
                            for (int i = 0; i < numbersOfTerms.length; i++) {
                                terms[i] = Integer.parseInt(numbersOfTerms[i]);
                            }
                        } else {
                            int fromTerm = Integer.parseInt(numbersOfTerms[0]), toTerm = Integer.parseInt(numbersOfTerms[1]);
                            terms = new int[toTerm - fromTerm + 1];
                            for (int j = fromTerm; j <= toTerm; j++) {
                                terms[j - fromTerm] = j;
                            }
                        }
                    } else if (StringUtils.isNotEmpty(numbersOfTerms[0].trim())) {
                        terms = new int[]{Integer.parseInt(numbersOfTerms[0].trim())};
                    }

                    lectureHours = holder.getInt(CalendarHeader.lectureHours).orElse(0);
                    laboratoryHours = holder.getInt(CalendarHeader.laboratoryHours).orElse(0);
                    seminarHours = holder.getInt(CalendarHeader.seminarHours).orElse(0);

                    isOptionalSubject = subjectName.startsWith("Дисциплина по выбору");
                    if (isOptionalSubject)
                        continue;
                }

                Optional<Subject> subjOpt = subjDao.findByName(subjectName);
                Subject subj;

                if (!subjOpt.isPresent()) {
                    subj = new Subject();
                    subj.setName(subjectName);
                    subjDao.create(subj);
                } else {
                    subj = subjOpt.get();
                }


                CalendarItem item = new CalendarItem();
                item.setSubject(subj);

                for (int term : terms) {
                    Optional<Term> termOpt = termDao.findByNumber(term);
                    if (termOpt.isPresent()) {
                        CalendarItemCell itemCell = new CalendarItemCell();
                        itemCell.setTerm(termOpt.get());

                        if (laboratoryCT.isPresent() && laboratoryHours > 0) {
                            HoursPerClass labHPC = new HoursPerClass();
                            labHPC.setClassType(laboratoryCT.get());
                            labHPC.setNoOfHours(laboratoryHours);
                            itemCell.addHoursPerClass(labHPC);
                        }
                        if (lectureCT.isPresent() && lectureHours > 0) {
                            HoursPerClass lecHPC = new HoursPerClass();
                            lecHPC.setClassType(lectureCT.get());
                            lecHPC.setNoOfHours(lectureHours);
                            itemCell.addHoursPerClass(lecHPC);
                        }
                        if (seminarCT.isPresent() && seminarHours > 0) {
                            HoursPerClass semHPC = new HoursPerClass();
                            semHPC.setClassType(seminarCT.get());
                            semHPC.setNoOfHours(seminarHours);
                            itemCell.addHoursPerClass(semHPC);
                        }

                        item.addItemCell(itemCell);
                    }
                }

                studyFlow.addCalendarItem(item);
            }
        }

        flowDao.update(studyFlow);
    }
}
