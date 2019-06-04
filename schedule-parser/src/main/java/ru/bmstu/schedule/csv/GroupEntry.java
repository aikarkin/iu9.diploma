package ru.bmstu.schedule.csv;

import java.util.Objects;

public class GroupEntry {

    private String specialityCode;
    private String facultyCipher;
    private String degreeName;
    private int specializationNumber;
    private int departmentNumber;
    private int termNumber;
    private int groupNumber;

    public GroupEntry() {
    }

    public String getSpecialityCode() {
        return specialityCode;
    }

    public void setSpecialityCode(String specialityCode) {
        this.specialityCode = specialityCode;
    }

    public String getFacultyCipher() {
        return facultyCipher;
    }

    public void setFacultyCipher(String facultyCipher) {
        this.facultyCipher = facultyCipher;
    }

    public int getSpecializationNumber() {
        return specializationNumber;
    }

    public void setSpecializationNumber(int specializationNumber) {
        this.specializationNumber = specializationNumber;
    }

    public int getDepartmentNumber() {
        return departmentNumber;
    }

    public void setDepartmentNumber(int departmentNumber) {
        this.departmentNumber = departmentNumber;
    }

    public int getTermNumber() {
        return termNumber;
    }

    public void setTermNumber(int termNumber) {
        this.termNumber = termNumber;
    }

    public int getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }

    public String getDegreeName() {
        return degreeName;
    }

    public void setDegreeName(String degreeName) {
        this.degreeName = degreeName;
    }

    public String getDepartmentCipher() {
        return String.format("%s%d", facultyCipher, departmentNumber);
    }

    public String getSpecializationCode() {
        return String.format("%s_%d", specialityCode, specializationNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupEntry that = (GroupEntry) o;
        return specializationNumber == that.specializationNumber &&
                departmentNumber == that.departmentNumber &&
                termNumber == that.termNumber &&
                groupNumber == that.groupNumber &&
                Objects.equals(specialityCode, that.specialityCode) &&
                Objects.equals(facultyCipher, that.facultyCipher) &&
                Objects.equals(degreeName, that.degreeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(specialityCode, facultyCipher, degreeName, specializationNumber, departmentNumber, termNumber, groupNumber);
    }

}
