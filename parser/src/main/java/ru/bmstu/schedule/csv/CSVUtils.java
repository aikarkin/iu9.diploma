package ru.bmstu.schedule.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.hibernate.SessionFactory;
import ru.bmstu.schedule.csv.parser.Parser;
import ru.bmstu.schedule.csv.parser.ParserFactory;
import ru.bmstu.schedule.dao.Dao;
import ru.bmstu.schedule.dao.HibernateDao;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.function.BiConsumer;

public class CSVUtils {

    public static <E, K extends Serializable> void
    fillFromCsv(Class<E> entityClass, Dao<K, E> dao, String csvFile, BiConsumer<E, RecordHolder> entityConsumer)
        throws IOException, NotImplementedException {

        CSVParser parser = CSVFormat.EXCEL.withHeader().parse(new FileReader(csvFile));
        Parser<E> entityParser = ParserFactory.parserFor(entityClass);


        for(CSVRecord rec : parser) {
            RecordHolder holder = new RecordHolder(rec);
            E parsed = entityParser.parse(holder);
            entityConsumer.accept(parsed, holder);
            dao.create(parsed);
        }
    }


    public static <E, K extends Serializable> void
    fillFromCsv(Class<E> entityClass, Dao<K, E> dao, String csvFile) throws IOException, NotImplementedException {
        fillFromCsv(entityClass, dao, csvFile, (e, r) -> {});
    }

}
