package ru.bmstu.schedule.smtgen.cli;

import java.util.List;
import java.util.Objects;

public class ScheduleConfiguration {

    private List<String> groupCiphers;
    private int noOfTerm = -1;
    private int enrollmentYear = -1;
    private String specializationCode;
    private String departmentCipher;

    public List<String> getGroupCiphers() {
        return groupCiphers;
    }

    public void setGroupCiphers(List<String> groupCiphers) {
        this.groupCiphers = groupCiphers;
    }

    public int getNoOfTerm() {
        return noOfTerm;
    }

    public void setNoOfTerm(int noOfTerm) {
        this.noOfTerm = noOfTerm;
    }

    public int getEnrollmentYear() {
        return enrollmentYear;
    }

    public void setEnrollmentYear(int enrollmentYear) {
        this.enrollmentYear = enrollmentYear;
    }

    public String getSpecializationCode() {
        return specializationCode;
    }

    public void setSpecializationCode(String specializationCode) {
        this.specializationCode = specializationCode;
    }

    public String getDepartmentCipher() {
        return departmentCipher;
    }

    public void setDepartmentCipher(String departmentCipher) {
        this.departmentCipher = departmentCipher;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleConfiguration that = (ScheduleConfiguration) o;
        return noOfTerm == that.noOfTerm &&
                enrollmentYear == that.enrollmentYear &&
                Objects.equals(groupCiphers, that.groupCiphers) &&
                Objects.equals(specializationCode, that.specializationCode) &&
                Objects.equals(departmentCipher, that.departmentCipher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupCiphers, noOfTerm, enrollmentYear, specializationCode, departmentCipher);
    }

}
