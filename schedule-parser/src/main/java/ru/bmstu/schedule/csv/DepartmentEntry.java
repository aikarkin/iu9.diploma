package ru.bmstu.schedule.csv;

import java.util.Objects;

public class DepartmentEntry {

    private String departmentTitle;
    private String facultyCipher;
    private int departmentNumber;

    public DepartmentEntry() {
    }

    public String getDepartmentTitle() {
        return departmentTitle;
    }

    public void setDepartmentTitle(String departmentTitle) {
        this.departmentTitle = departmentTitle;
    }

    public String getFacultyCipher() {
        return facultyCipher;
    }

    public void setFacultyCipher(String facultyCipher) {
        this.facultyCipher = facultyCipher;
    }

    public int getDepartmentNumber() {
        return departmentNumber;
    }

    public void setDepartmentNumber(int departmentNumber) {
        this.departmentNumber = departmentNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepartmentEntry that = (DepartmentEntry) o;
        return departmentNumber == that.departmentNumber &&
                Objects.equals(departmentTitle, that.departmentTitle) &&
                Objects.equals(facultyCipher, that.facultyCipher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(departmentTitle, facultyCipher, departmentNumber);
    }

}
