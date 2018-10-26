package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "dep_to_spec", schema = "public", catalog = "schedule")
//@AssociationOverrides({
//        @AssociationOverride(name= "compositeKey.department", joinColumns = @JoinColumn(name="department_id")),
//        @AssociationOverride(name= "compositeKey.specialization", joinColumns = @JoinColumn(name="spec_id"))
//})
class DepartmentSpecialization {
    private int id;

    // embedded composite key
//    private DepSpecId compositeKey = new DepSpecId();

    private Collection<StudyFlow> studyFlows;

    private Department department;

    private Specialization specialization;

//    @Embedded
//    DepSpecId getCompositeKey() {
//        return compositeKey;
//    }
//
//    void setCompositeKey(DepSpecId primaryKey) {
//        this.compositeKey = primaryKey;
//    }

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }
//
//    @Transient
//    Department getDepartment() {
//        return getCompositeKey().getDepartment();
//    }
//
//    void setDepartment(Department department) {
//        getCompositeKey().setDepartment(department);
//    }
//
//    @Transient
//    Specialization getSpecialization() {
//        return  getCompositeKey().getSpecialization();
//    }
//
//    void setSpecialization(Specialization specialization) {
//        getCompositeKey().setSpecialization(specialization);
//    }


    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name="department_id")
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name="spec_id")
    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    @OneToMany(mappedBy = "departmentSpecialization", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Collection<StudyFlow> getStudyFlows() {
        return studyFlows;
    }

    void setStudyFlows(Collection<StudyFlow> studyFlows) {
        this.studyFlows = studyFlows;
    }
}
