package ru.bmstu.schedule.dbtools;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jsoup.HttpStatusException;
import ru.bmstu.schedule.dao.LecturerDao;
import ru.bmstu.schedule.dao.StudyGroupDao;
import ru.bmstu.schedule.entity.CalendarItem;
import ru.bmstu.schedule.entity.DepartmentSubject;
import ru.bmstu.schedule.entity.Lecturer;
import ru.bmstu.schedule.entity.StudyGroup;
import ru.bmstu.schedule.html.node.GroupNode;
import ru.bmstu.schedule.html.node.ScheduleItemParityNode;
import ru.bmstu.schedule.html.parser.ScheduleParser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DownloadReferences {

    private static final String SCHEDULE_BASE_URL = "https://students.bmstu.ru/";
    private static final String OUT_DIR = "/home/alex/dev/src/iu9/schedule/dbfill/src/main/resources/references";
    private static final String ROOMS_REF_NAME = "rooms.csv";
    private static final String LEC_SUBJ_REF = "lecturers_subjects.iu9.csv";


    private static final String[] LEC_SUBJ_HEADERS = new String[]{
            "lecturer",
            "subjects",
            "department"
    };
    private static final String[] ROOMS_HEADERS = new String[]{
            "roomNo"
    };


    public static void main(String[] args) {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        try {
            ScheduleParser scheduleParser = new ScheduleParser(SCHEDULE_BASE_URL);
            createLecturerSubjectsReference(sessionFactory, scheduleParser);
            createRoomsReference(scheduleParser);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            sessionFactory.close();
        }

    }

    private static void createRoomsReference(ScheduleParser scheduleParser) throws IOException {
        System.out.println("[info] Filling rooms ...");

        FileWriter out = new FileWriter(OUT_DIR + "/" + ROOMS_REF_NAME);
        Set<String> classRoomsSet = new HashSet<>();

        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(ROOMS_HEADERS))) {
            for (GroupNode groupNode : scheduleParser.getAllGroups()) {
                try {
                    scheduleParser
                            .scheduleTravellerFor(groupNode)
                            .entitiesListOf(ScheduleItemParityNode.class)
                            .forEach(itemParity -> {
                                try {
                                    String roomNo = itemParity.getClassroom();
                                    if (StringUtils.isNotEmpty(roomNo) && !classRoomsSet.contains(roomNo)) {
                                        printer.printRecord(roomNo);
                                        classRoomsSet.add(roomNo);
                                        System.out.println("[info] Saved classroom: " + roomNo);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    System.out.println("[error] Failed to save csv row for itemParity: " + itemParity);
                                }
                            });
                } catch (HttpStatusException e) {
                    e.printStackTrace();
                    System.out.println("[error] Failed to fetch item parities for group: " + groupNode.getCipher());
                }
            }
        }

        System.out.println("[info] Rooms successful saved.");
    }

    private static void createLecturerSubjectsReference(SessionFactory sessionFactory, ScheduleParser scheduleParser) throws IOException {
        System.out.println("[info] Filling lecturers' subjects ...");
        StudyGroupDao groupDao = new StudyGroupDao(sessionFactory);
        LecturerDao lecDao = new LecturerDao(sessionFactory);
        Set<String> rowsSet = new HashSet<>();

        FileWriter out = new FileWriter(OUT_DIR + "/" + LEC_SUBJ_REF);
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(LEC_SUBJ_HEADERS))) {
            for (GroupNode groupNode : scheduleParser.getAllGroups()) {
                Optional<StudyGroup> groupOpt = groupDao.findByCipher(groupNode.getCipher());
                Map<String, DepartmentSubject> subjNameToDeptSubj = new HashMap<>();
                if (!groupOpt.isPresent()) {
                    System.out.println("[warn] No such group with cipher: " + groupNode.getCipher());
                    continue;
                }

                for (CalendarItem calendarItem : groupOpt.get().getCalendar().getCalendarItems()) {
                    DepartmentSubject deptSubj = calendarItem.getDepartmentSubject();
                    subjNameToDeptSubj.put(deptSubj.getSubject().getName(), deptSubj);
                }

                try {

                    scheduleParser
                            .scheduleTravellerFor(groupNode)
                            .entitiesListOf(ScheduleItemParityNode.class)
                            .forEach(itemParity -> {
                                String lecturerInitials = itemParity.getLecturer();
                                String subjectName = itemParity.getSubject();

                                DepartmentSubject deptSubj = subjNameToDeptSubj.get(subjectName);
                                List<Lecturer> lecList = lecDao.findByInitials(lecturerInitials);
                                if (lecList.size() == 0) {
                                    System.out.println("[warn] Lecturer with such initials not found: " + lecturerInitials);
                                    return;
                                }
                                try {
                                    if(deptSubj == null || deptSubj.getDepartment() == null) {
                                        System.out.println("[error] No subject department provided: " + subjectName);
                                        return;
                                    }
                                    String[] row = new String[]{
                                            lecturerName(lecList.get(0)),
                                            String.format("%s (%s.)", subjectName, itemParity.getClassType().substring(0, 3)),
                                            deptSubj.getDepartment().getCipher()
                                    };
                                    String rowStr = String.join(";", row);
                                    if (!rowsSet.contains(rowStr)) {
                                        rowsSet.add(rowStr);
                                        printer.printRecord((Object[]) row);
                                        System.out.println("Saved row: " + rowStr);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    System.out.println("[error] Failed to save subject: " + subjectName);
                                }
                            });
                } catch (HttpStatusException e) {
                    e.printStackTrace();
                    System.out.println("[error] Failed to fetch item parities for group: " + groupNode.getCipher());
                }
            }
        }

        System.out.println("[info] Subjects successful saved");
    }

    private static String lecturerName(Lecturer lecturer) {
        return String.format(
                "%s %s. %s.",
                lecturer.getLastName(),
                lecturer.getFirstName().toUpperCase().charAt(0),
                lecturer.getMiddleName().toUpperCase().charAt(0)
        );
    }

}
