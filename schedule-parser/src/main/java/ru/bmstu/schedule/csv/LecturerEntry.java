package ru.bmstu.schedule.csv;

import java.util.Objects;
import java.util.Set;

public class LecturerEntry {

    private String firstName;
    private String lastName;
    private String middleName;

    private String eduDegree;

    private Set<String> specializationsCodes;
    private Set<String> subjectsNames;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getEduDegree() {
        return eduDegree;
    }

    public void setEduDegree(String eduDegree) {
        this.eduDegree = eduDegree;
    }

    public Set<String> getSpecializationsCodes() {
        return specializationsCodes;
    }

    public void setSpecializationsCodes(Set<String> specializationsCodes) {
        this.specializationsCodes = specializationsCodes;
    }

    public Set<String> getSubjectsNames() {
        return subjectsNames;
    }

    public void setSubjectsNames(Set<String> subjectsNames) {
        this.subjectsNames = subjectsNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LecturerEntry that = (LecturerEntry) o;
        return Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(middleName, that.middleName) &&
                Objects.equals(eduDegree, that.eduDegree) &&
                Objects.equals(specializationsCodes, that.specializationsCodes) &&
                Objects.equals(subjectsNames, that.subjectsNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, middleName, eduDegree, specializationsCodes, subjectsNames);
    }

    @Override
    public String toString() {
        return "LecturerEntry{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", eduDegree='" + eduDegree + '\'' +
                ", specializationsCodes=" + specializationsCodes +
                ", subjectsNames=" + subjectsNames +
                '}';
    }

}
