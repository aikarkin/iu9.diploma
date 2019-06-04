package ru.bmstu.schedule.csv;

import java.util.Objects;

public class SpecializationEntry {

    private String specialityCode;
    private String specialityName;

    private String specializationName;
    private int numberInSpeciality;

    private String degreeName;
    private int degreeStudyYears;

    public SpecializationEntry() {
    }

    public String getSpecialityCode() {
        return specialityCode;
    }

    public void setSpecialityCode(String specialityCode) {
        this.specialityCode = specialityCode;
    }

    public String getSpecialityName() {
        return specialityName;
    }

    public void setSpecialityName(String specialityName) {
        this.specialityName = specialityName;
    }

    public String getSpecializationName() {
        return specializationName;
    }

    public void setSpecializationName(String specializationName) {
        this.specializationName = specializationName;
    }

    public int getNumberInSpeciality() {
        return numberInSpeciality;
    }

    public void setNumberInSpeciality(int numberInSpeciality) {
        this.numberInSpeciality = numberInSpeciality;
    }

    public String getDegreeName() {
        return degreeName;
    }

    public void setDegreeName(String degreeName) {
        this.degreeName = degreeName;
    }

    public int getDegreeStudyYears() {
        return degreeStudyYears;
    }

    public void setDegreeStudyYears(int degreeStudyYears) {
        this.degreeStudyYears = degreeStudyYears;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpecializationEntry that = (SpecializationEntry) o;
        return numberInSpeciality == that.numberInSpeciality &&
                degreeStudyYears == that.degreeStudyYears &&
                Objects.equals(specialityCode, that.specialityCode) &&
                Objects.equals(specialityName, that.specialityName) &&
                Objects.equals(specializationName, that.specializationName) &&
                Objects.equals(degreeName, that.degreeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(specialityCode, specialityName, specializationName, numberInSpeciality, degreeName, degreeStudyYears);
    }

}
