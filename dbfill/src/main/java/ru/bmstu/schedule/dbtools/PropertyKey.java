package ru.bmstu.schedule.dbtools;

import javax.naming.ConfigurationException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class PropertyKey {

    public static final String REF_CLASS_TYPE = "csv.file.classtypes";
    public static final String REF_WEEKS = "csv.file.week";
    public static final String REF_DEPARTMENTS = "csv.file.departments";
    public static final String REF_FACULTIES = "csv.file.faculties";
    public static final String REF_GROUPS = "csv.file.groups";
    public static final String REF_LECTURERS = "csv.file.lecturers";
    public static final String REF_SPECS = "csv.file.specs";
    public static final String REF_CLASS_TIME = "csv.file.classtime";
    public static final String REF_FOLDER_CALENDAR = "csv.folder.calendar";
    public static final String REF_SUBJECTS = "csv.file.subjects";
    public static final String REF_ROOMS = "csv.file.rooms";

    private static final List<String> REQUIRED_KEYS = Arrays.asList(
            REF_CLASS_TYPE,
            REF_WEEKS,
            REF_DEPARTMENTS,
            REF_LECTURERS,
            REF_SPECS,
            REF_GROUPS
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
