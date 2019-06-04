package ru.bmstu.schedule.csv;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LecturerSubjectEntry {

    private String department;
    private String lecturer;
    private List<Map.Entry<ClassKind, String>> subjectsOfKind;

    public LecturerSubjectEntry() {
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public List<Map.Entry<ClassKind, String>> getSubjectsOfKind() {
        return subjectsOfKind;
    }

    public void setSubjectsOfKind(List<Map.Entry<ClassKind, String>> subjectsOfKind) {
        this.subjectsOfKind = subjectsOfKind;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LecturerSubjectEntry that = (LecturerSubjectEntry) o;
        return Objects.equals(department, that.department) &&
                Objects.equals(lecturer, that.lecturer) &&
                Objects.equals(subjectsOfKind, that.subjectsOfKind);
    }

    @Override
    public int hashCode() {
        return Objects.hash(department, lecturer, subjectsOfKind);
    }

    public enum ClassKind {
        lec,
        sem,
        lab,
        any;

        private static final BiMap<String, ClassKind> nameToKind;

        static {
            nameToKind = HashBiMap.create();
            nameToKind.put("лек", lec);
            nameToKind.put("сем", sem);
            nameToKind.put("лаб", lab);
        }

        public static ClassKind kindByShortName(String name) {
            return nameToKind.getOrDefault(name, any);
        }

        public static String shortNameByKind(ClassKind classKind) {
            return nameToKind.inverse().get(classKind);
        }
    }

}
