package ru.bmstu.schedule.entity;

import org.hibernate.HibernateException;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "study_flow")
public class StudyFlow {
    private int id;
    private int enrollmentYear;
    private Set<CalendarItem> calendarItems = new HashSet<>();
    private DepartmentSpecialization departmentSpecialization = new DepartmentSpecialization();
    private Set<StudyGroup> studyGroups;

    public StudyFlow() {}

    @ManyToOne
    @JoinColumn(name="dep_to_spec_id", referencedColumnName = "id")
    DepartmentSpecialization getDepartmentSpecialization() {
        return departmentSpecialization;
    }

    void setDepartmentSpecialization(DepartmentSpecialization depSpecializations) {
        this.departmentSpecialization = depSpecializations;
    }

    public void setDeparmtentAndSpecialization(Department department, Specialization specialization) {
        for(DepartmentSpecialization depSpec : department.getDepartmentSpecializations()) {
            if(depSpec.getSpecialization().equals(specialization)) {
                this.setDepartmentSpecialization(depSpec);
                return;
            }
        }
        String msg = String.format(
                "StudyFlows: Invalid action - unable to add study flow to department '%s' with specialization '%s'. Current specialization is not belong to this department.",
                department.getCipher(),
                specialization.getCode()
        );
        throw new HibernateException(msg);
    }

    @Transient
    public Department getDepartment() {
        return this.getDepartmentSpecialization().getDepartment();
    }

    @Transient
    public Specialization getSpecialization() {
        return this.getDepartmentSpecialization().getSpecialization();
    }

    @Id
    @Column(name = "flow_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "start_year", nullable = false)
    public int getEnrollmentYear() {
        return enrollmentYear;
    }

    public void setEnrollmentYear(int enrollmentYear) {
        this.enrollmentYear = enrollmentYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudyFlow studyFlow = (StudyFlow) o;
        return id == studyFlow.id &&
                enrollmentYear == studyFlow.enrollmentYear;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, enrollmentYear);
    }

    @OneToMany(mappedBy = "studyFlow", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public Set<CalendarItem> getCalendarItems() {
        return calendarItems;
    }

    public void setCalendarItems(Set<CalendarItem> calendarItems) {
        this.calendarItems = calendarItems;
    }

    @OneToMany(mappedBy = "studyFlow", fetch = FetchType.EAGER)
    public Set<StudyGroup> getStudyGroups() {
        return studyGroups;
    }

    public void setStudyGroups(Set<StudyGroup> studyGroups) {
        this.studyGroups = studyGroups;
    }

    public void addCalendarItem(CalendarItem item) {
        item.setStudyFlow(this);
        getCalendarItems().add(item);
    }

    @Override
    public String toString() {
        return "StudyFlow{" +
                "id=" + id +
                ", enrollmentYear=" + enrollmentYear +
                '}';
    }
}
