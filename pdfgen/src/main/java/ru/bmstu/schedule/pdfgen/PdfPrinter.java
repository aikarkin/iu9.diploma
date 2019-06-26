package ru.bmstu.schedule.pdfgen;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.bmstu.schedule.dao.ClassTimeDao;
import ru.bmstu.schedule.dao.StudyGroupDao;
import ru.bmstu.schedule.entity.ClassTime;
import ru.bmstu.schedule.entity.StudyGroup;

import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class PdfPrinter {

    private static final String GROUP_RE = "(\\p{Lu}+)(\\d+)?-\\d{2,}";

    public static void main(String[] args) {
        if(args.length < 2) {
            System.err.printf("Невалидное число аргументов: %d (требуется %d)%n", args.length, 2);
            return;
        }

        try (SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory()) {
            StudyGroupDao groupDao = new StudyGroupDao(sessionFactory);
            ClassTimeDao ctDao = new ClassTimeDao(sessionFactory);
            List<ClassTime> classTimes = ctDao.findAll();

            String outDir = args[0];
            String groupCipher = args[1];

            if(!groupCipher.matches(GROUP_RE)) {
                System.err.println("Невалидное значение группы: " + groupCipher);
                return;
            }

            Optional<StudyGroup> groupOpt = groupDao.findByCipher(groupCipher);

            if (!groupOpt.isPresent()) {
                System.err.println("Группа с таким шифром не найдена в базе: " + groupCipher);
                return;
            }

            StudyGroup group = groupOpt.get();
            try {

                String outFile = Paths.get(outDir, String.format("%s.pdf", groupCipher)).toString();
                PDFUtils.exportToPdf(classTimes, group, outFile);
                System.out.printf("PDF-документ с раписаниием для группы '%s' успешно сгенерирован: %s%n", groupCipher, outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.err.println("Не удалось открыть указанную директорию: " + outDir);
            }
        }
    }

}
