package ru.bmstu.schedule.pdfgen;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import ru.bmstu.schedule.entity.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class PDFUtils {

    private static final String FREE_SANS_FONT_PATH = "/font/FreeSans.ttf";
    private static final float[] TABLE_COLUMN_WIDTHS = {64, 208, 208};
    private static final float TABLE_WIDTH = 480;
    private static final float TABLE_MARGIN_TOP = 10;
    private static final float TABLE_MARGIN_BOTTOM = 10;
    private static final int DAYS_PER_PAGE = 3;
    private static PdfFont font;

    static {
        try {
            font = PdfFontFactory.createFont(FREE_SANS_FONT_PATH, "Cp1251", true);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }


    public static void exportToPdf(StudyGroup group, String filePath) throws FileNotFoundException {
        File outFile = new File(filePath);
        System.out.println("file path: " + filePath);
        String[] slash = outFile.getAbsolutePath().split("/");
        File baseDir = new File(String.join("/", Arrays.copyOfRange(slash, 0, slash.length - 2)));

        System.out.println("dir path: " + baseDir.getAbsolutePath());

        if (baseDir.exists() && baseDir.isDirectory()) {
            System.out.println("directory exists");
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
            Document doc = new Document(pdfDoc);
            doc.setTextAlignment(TextAlignment.CENTER);

            doc.add(docHeaderParagraph(cipherOf(group)));

            int noOfWeak = 0;

            List<ScheduleDay> dayList = new ArrayList<>(group.getScheduleDays());

            final List<String> WEEK_ORDER = Arrays.asList("ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ");

            dayList.sort((d1, d2) -> {
                int d1Idx = WEEK_ORDER.indexOf(d1.getDayOfWeek().getShortName().trim());
                int d2Idx = WEEK_ORDER.indexOf(d2.getDayOfWeek().getShortName().trim());

                return d1Idx - d2Idx;
            });

            System.out.println(dayList);

            for (ScheduleDay day : dayList) {
                if (noOfWeak == DAYS_PER_PAGE)
                    doc.add(new AreaBreak());
                appendScheduleDay(doc, day);
                noOfWeak++;
            }

            doc.close();
        } else {
            System.out.println("[error] Matched directory not exists: " + baseDir);
        }
    }

    private static void appendScheduleDay(Document doc, ScheduleDay scheduleDay) {
        Table table = new Table(TABLE_COLUMN_WIDTHS);
        table.setWidth(TABLE_WIDTH);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        table.setMarginTop(TABLE_MARGIN_TOP);
        table.setMarginBottom(TABLE_MARGIN_BOTTOM);

        table.addHeaderCell(cellHeaderParagraph("Время"));
        table.addHeaderCell(cellHeaderParagraph("ЧС"));
        table.addHeaderCell(cellHeaderParagraph("ЗН"));

        String parityType;

        List<ScheduleItem> items = new ArrayList<>(scheduleDay.getScheduleItems());
        items.sort(Comparator.comparing(c -> c.getClassTime().getStartsAt()));

        for (ScheduleItem item : items) {
            ClassTime ct = item.getClassTime();
            if (ct == null)
                continue;

            table.addCell(cellParagraph(ct.toString()));
            List<ScheduleItemParity> parities = new ArrayList<>(item.getScheduleItemParities());
            parities.sort((p1, p2) -> {
                if (p1.getDayParity().equals("ЧС") && p2.getDayParity().equals("ЗН"))
                    return 1;
                else if (p1.getDayParity().equals("ЗН") && p2.getDayParity().equals("ЧС"))
                    return -1;

                return 0;
            });

            System.out.println(item);

            if (parities.size() == 1) {
                ScheduleItemParity parity = parities.get(0);
                System.out.println("\t" + parity.toString());

                parityType = parity.getDayParity().trim();

                if (parityType.equals("ЧС/ЗН")) {
                    table.addCell(mergedCell(1, 2, readableItemParity(parity)));
                } else if (parityType.equals("ЧС")) {
                    table.addCell(cellParagraph(readableItemParity(parity)));
                    table.addCell(emptyParagraph());
                } else if (parityType.equals("ЗН")) {
                    table.addCell(emptyParagraph());
                    table.addCell(cellParagraph(readableItemParity(parity)));
                }
            } else if (parities.size() == 2) {

                ScheduleItemParity parity1 = parities.get(0), parity2 = parities.get(1);

                System.out.println("\t" + parity1.toString());
                System.out.println("\t" + parity2.toString());

                if (parity1.getDayParity().trim().equals("ЧС")) {
                    table.addCell(cellParagraph(readableItemParity(parity1)));
                    table.addCell(cellParagraph(readableItemParity(parity2)));
                } else if (parity2.getDayParity().trim().equals("ЧС")) {
                    table.addCell(cellParagraph(readableItemParity(parity2)));
                    table.addCell(cellParagraph(readableItemParity(parity1)));
                }
            } else {
                table.addCell(mergedCell(1, 2, ""));
            }

            table.startNewRow();
            System.out.println();
        }

        doc.add(dayHeaderParagraph(scheduleDay.getDayOfWeek().getShortName()));
        doc.add(table);

    }

    private static String readableItemParity(ScheduleItemParity itemParity) {
        StringBuilder builder = new StringBuilder();
        ClassType ct = itemParity.getClassType();
        Classroom cr = itemParity.getClassroom();
        TutorSubject tutorSubject = itemParity.getTutorSubject();

        Subject subj = tutorSubject.getDepartmentSubject().getSubject();
        Tutor lec = tutorSubject.getTutor();

        if (ct != null) {
            builder.append("(")
                    .append(ct.getName(), 0, 3)
                    .append(")  ");
        }
        if (subj != null) {
            builder.append(subj.getName())
                    .append("  ");
        }

        if (cr != null) {
            builder.append(cr.getRoomNumber())
                    .append("  ");
        }


        builder.append(lec.getLastName().equals("[UNKNOWN]") ? "" : lec.getInitials());

        return builder.toString();
    }

    private static String cipherOf(StudyGroup group) {
        StudyPlan studyPlan = group.getStudyPlan();
        EduDegree degree = studyPlan
                .getDepartmentSpecialization()
                .getSpecialization()
                .getSpeciality()
                .getDegree();
        char degreeLetter = degree.getName().toUpperCase().charAt(0);
        return String.format(
                "%s-%d%d%s",
                studyPlan.getDepartmentSpecialization().getDepartment().getCipher(),
                group.getTerm().getNumber(),
                group.getNumber(),
                (degreeLetter == 'Б' ? "" : degreeLetter)
        );
    }

    private static Paragraph docHeaderParagraph(String text) {
        return new Paragraph(text)
                .setFont(font)
                .setFontSize(20)
                .setBold()
                .setMarginBottom(15);
    }

    private static Paragraph cellHeaderParagraph(String text) {
        return new Paragraph(text).setFont(font).setItalic();
    }

    private static Paragraph dayHeaderParagraph(String text) {
        return new Paragraph(text).setFont(font).setBold();
    }

    private static Paragraph cellParagraph(String text) {
        return new Paragraph(text)
                .setFont(font)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private static Cell mergedCell(int rowspan, int colspan, String text) {
        return new Cell(rowspan, colspan).add(cellParagraph(text));
    }

    private static Paragraph emptyParagraph() {
        return new Paragraph("");
    }

}
