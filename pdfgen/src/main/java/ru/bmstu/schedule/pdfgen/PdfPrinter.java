package ru.bmstu.schedule.pdfgen;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.bmstu.schedule.dao.StudyGroupDao;
import ru.bmstu.schedule.entity.StudyGroup;

import java.io.FileNotFoundException;
import java.util.Optional;

public class PdfPrinter {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Invalid arguments");
            return;
        }

        try (SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory()) {
            StudyGroupDao groupDao = new StudyGroupDao(sessionFactory);

            if(args.length < 2) {
                System.out.printf("[error] Invalid number of arguments: actual - %d, required - %d%n", args.length, 2);
                return;
            }

            String outDir = args[0];
            String groupCipher = args[1];

            Optional<StudyGroup> groupOpt = groupDao.findByCipher(groupCipher);

            if (!groupOpt.isPresent()) {
                System.out.println("[error] Group with cipher '" + groupCipher + "' was not found in database, sorry.");
                return;
            }

            StudyGroup group = groupOpt.get();
            System.out.println("[info] Found matched group: " + group.toString());
            try {
                String outFile = outDir + "/" + groupCipher + ".pdf";
                PDFUtils.exportToPdf(group, outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("[error] Failed to export group schedule to pdf.");
            }
        }
    }

}
