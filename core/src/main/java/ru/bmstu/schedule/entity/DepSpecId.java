package ru.bmstu.schedule.entity;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class DepSpecId implements Serializable {
    private Department department;
    private Specialization specialization;

    @ManyToOne(/*cascade = CascadeType.ALL*/)
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @ManyToOne(/*cascade = CascadeType.ALL*/)
    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

}
