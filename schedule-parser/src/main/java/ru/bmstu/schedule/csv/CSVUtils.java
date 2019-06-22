package ru.bmstu.schedule.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import ru.bmstu.schedule.csv.header.CalendarHeader;
import ru.bmstu.schedule.csv.header.LecturerHeader;
import ru.bmstu.schedule.csv.parser.EntryParser;
import ru.bmstu.schedule.csv.parser.ParserFactory;
import ru.bmstu.schedule.dao.*;
import ru.bmstu.schedule.entity.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;
import java.util.function.BiConsumer;

public class CSVUtils {

    private static final String LABORATORY_TYPE_NAME = "лабораторная работа";
    private static final String LECTURER_TYPE_NAME = "лекция";
    private static final String SEMINAR_TYPE_NAME = "семинар";

    @SuppressWarnings("unchecked")
    public static <E, K extends Serializable> void
    fillFromCsv(HibernateDao<K, E> dao, String csvFile, BiConsumer<E, RecordHolder> entityConsumer)
            throws IOException, IllegalStateException {

        CSVParser parser = CSVFormat.EXCEL.withHeader().parse(new FileReader(csvFile));
        EntryParser<E, ?> entityParser = ParserFactory.parserFor(dao.getPersistentClass());

        for (CSVRecord rec : parser) {
            RecordHolder holder = new RecordHolder(rec);
            E parsed = (E) entityParser.parse(holder);
            entityConsumer.accept(parsed, holder);
            dao.create(parsed);
        }
    }

    public static <E, K extends Serializable> void
    fillFromCsv(HibernateDao<K, E> dao, String csvFile) throws IOException, IllegalStateException {
        fillFromCsv(dao, csvFile, (e, r) -> {
        });
    }

    @SuppressWarnings("unchecked")
    public static void fillLecturers(String csvFile, SessionFactory sessionFactory) throws IOException {
        CSVParser parser = CSVFormat.EXCEL.withHeader().parse(new FileReader(csvFile));
        TutorDao tutorDao = new TutorDao(sessionFactory);
        EntryParser<LecturerEntry, LecturerHeader> lecParser = (EntryParser<LecturerEntry, LecturerHeader>) ParserFactory.parserFor(LecturerEntry.class);

        for (CSVRecord record : parser) {
            RecordHolder<LecturerHeader> recHolder = new RecordHolder<>(record);
            LecturerEntry lec;
            try {
                lec = lecParser.parse(recHolder);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("[error] Failed to parse csv record: " + record);
                continue;
            }

            Tutor lecEntity = new Tutor();
            lecEntity.setScienceDegree(lec.getEduDegree());
            lecEntity.setFirstName(lec.getFirstName());
            lecEntity.setLastName(lec.getLastName());
            lecEntity.setMiddleName(lec.getMiddleName());
            tutorDao.create(lecEntity);
        }
    }

    @SuppressWarnings("unchecked")
    public static void fillCalendar(StudyPlan studyPlan, SessionFactory sessionFactory, String csvFile) throws IOException {
        CSVParser parser = CSVFormat.EXCEL.withHeader().parse(new FileReader(csvFile));
        ClassTypeDao ctDao = new ClassTypeDao(sessionFactory);
        DepartmentSubjectDao deptSubjDao = new DepartmentSubjectDao(sessionFactory);
        DepartmentDao deptDao = new DepartmentDao(sessionFactory);
        SubjectDao subjDao = new SubjectDao(sessionFactory);
        TermDao termDao = new TermDao(sessionFactory);
        StudyPlanDao studyPlanDao = new StudyPlanDao(sessionFactory);

        boolean isOptionalSubject = false;
        String subjectName;
        int lectureHours = -1, seminarHours = -1, laboratoryHours = -1;
        int[] terms = new int[]{};

        Optional<ClassType> laboratoryCT = ctDao.findByName(LABORATORY_TYPE_NAME),
                seminarCT = ctDao.findByName(SEMINAR_TYPE_NAME),
                lectureCT = ctDao.findByName(LECTURER_TYPE_NAME);

        for (CSVRecord rec : parser) {
            RecordHolder<CalendarHeader> holder = new RecordHolder<>(rec);

            subjectName = holder.get(CalendarHeader.subject);

            if (subjectName.contains("Курсовая работа")) {
                continue;
            }

            if (StringUtils.isNotEmpty(subjectName)) {
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

                    lectureHours = holder.getInt(CalendarHeader.lectureHours).orElse(0) / terms.length;
                    laboratoryHours = holder.getInt(CalendarHeader.laboratoryHours).orElse(0) / terms.length;
                    seminarHours = holder.getInt(CalendarHeader.seminarHours).orElse(0) / terms.length;

                    isOptionalSubject = subjectName.startsWith("Дисциплина по выбору");
                    if (isOptionalSubject)
                        continue;
                }

                String deptCipher = holder.get(CalendarHeader.department);

                Optional<Department> deptOpt = deptDao.findByCipher(deptCipher);
                if (!deptOpt.isPresent()) {
                    System.out.println("[warn] Department not found: " + deptCipher);
                    continue;
                }

                Optional<Subject> subjOpt = subjDao.findByName(subjectName);
                Subject subj;
                if (subjOpt.isPresent()) {
                    subj = subjOpt.get();
                } else {
                    subj = new Subject(subjectName);
                    subj = subjDao.findByKey(subjDao.create(subj));
                }

                DepartmentSubject deptSubj;
                Optional<DepartmentSubject> deptSubjOpt = deptSubjDao.findByDepartmentAndSubject(deptOpt.get(), subj);

                if (deptSubjOpt.isPresent()) {
                    deptSubj = deptSubjOpt.get();
                } else {
                    deptSubj = new DepartmentSubject(deptOpt.get(), subj);
                    Integer deptSubjId = deptSubjDao.create(deptSubj);
                    deptSubj.setId(deptSubjId);
                }

                StudyPlanItem item = new StudyPlanItem();
                item.setDepartmentSubject(deptSubj);

                for (int termNo : terms) {
                    Optional<Term> termOpt = termDao.findByNumber(termNo);
                    Term term;
                    if (termOpt.isPresent()) {
                        term = termOpt.get();
                    } else {
                        term = new Term();
                        term.setNumber(termNo);
                        Integer termId = termDao.create(term);
                        term.setId(termId);
                    }
                    StudyPlanItemCell itemCell = new StudyPlanItemCell();
                    itemCell.setTerm(term);

                    int[] hoursPerCT = new int[]{laboratoryHours, lectureHours, seminarHours};
                    Optional<ClassType>[] classTypes = new Optional[]{laboratoryCT, lectureCT, seminarCT};

                    for (int i = 0; i < hoursPerCT.length; i++) {
                        Optional<ClassType> ctOpt = classTypes[i];
                        int hours = hoursPerCT[i];
                        if (ctOpt.isPresent() && hours > 0) {
                            HoursPerClass hpc = new HoursPerClass();
                            hpc.setClassType(ctOpt.get());
                            hpc.setNoOfHours(hours);
                            itemCell.addHoursPerClass(hpc);
                        }
                    }

                    item.addItemCell(itemCell);

                }

                studyPlan.addCalendarItem(item);
            }
        }

        studyPlanDao.update(studyPlan);
    }

}
