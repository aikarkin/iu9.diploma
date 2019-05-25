package ru.bmstu.schedule.dbtools;

import javax.naming.ConfigurationException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class PropertyKey {

    public static final String SCHEDULE_BASE_URL = "schedule.baseurl";
    public static final String REF_CLASS_TYPE = "csv.file.classtypes";
    public static final String REF_WEEKS = "csv.file.week";
    public static final String REF_DEPARTMENTS = "csv.file.departments";
    public static final String REF_DEGREES = "csv.file.degrees";
    public static final String REF_LECTURERS = "csv.file.lecturers";
    public static final String REF_SPECDEPS = "csv.file.specdeps";
    public static final String REF_SPECS = "csv.file.speccodes";
    public static final String REF_CLASS_TIME = "csv.file.classtime";
    public static final String REF_FOLDER_CALENDAR = "csv.folder.calendar";

    private static final List<String> REQUIRED_KEYS = Arrays.asList(
            SCHEDULE_BASE_URL,
            REF_CLASS_TYPE,
            REF_WEEKS,
            REF_DEPARTMENTS,
            REF_DEGREES,
            REF_LECTURERS,
            REF_SPECS,
            REF_SPECDEPS
    );

    private PropertyKey() {
    }

    public static void validateProperties(Properties props) throws ConfigurationException {
        for (String key : REQUIRED_KEYS) {
            if (props.getProperty(key) == null)
                throw new ConfigurationException("Key '" + key + "' is absent.");
        }
    }

}
