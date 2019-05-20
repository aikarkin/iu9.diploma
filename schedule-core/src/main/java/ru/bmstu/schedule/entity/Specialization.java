package ru.bmstu.schedule.entity;

import org.hibernate.HibernateException;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Specialization {
    private int id;
    private String code;
    private String title;
    private EduDegree eduDegree;

    private Set<DepartmentSpecialization> departmentSpecializations = new HashSet<>();

    @Id
    @Column(name = "spec_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "spec_code", columnDefinition = "bpchar", length = 8)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "title", nullable = false, length = -1)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ManyToOne
    @JoinColumn(name = "degree_id", referencedColumnName = "degree_id")
    public EduDegree getEduDegree() {
        return eduDegree;
    }

    public void setEduDegree(EduDegree eduDegree) {
        this.eduDegree = eduDegree;
    }

//    @OneToMany(mappedBy = "compositeKey.specialization", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OneToMany(mappedBy = "specialization", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Set<DepartmentSpecialization> getDepartmentSpecializations() {
        return departmentSpecializations;
    }

    void setDepartmentSpecializations(Set<DepartmentSpecialization> departmentSpecializations) {
        this.departmentSpecializations = departmentSpecializations;
    }

    @Transient
    public List<Department> getDepartments() {
        return getDepartmentSpecializations()
                .stream()
                .map(DepartmentSpecialization::getDepartment)
                .collect(Collectors.toList());
    }

    @Transient
    public List<StudyFlow> getStudyFlows() {
        return getDepartmentSpecializations()
                .stream()
                .flatMap(ds -> ds.getStudyFlows().stream())
                .collect(Collectors.toList());
    }

    public void addDepartment(Department dep) {
        DepartmentSpecialization depSpec = new DepartmentSpecialization();
        depSpec.setDepartment(dep);
        depSpec.setSpecialization(this);
        getDepartmentSpecializations().add(depSpec);
    }

    public void addStudyFlow(StudyFlow studyFlow, Department dep) {
        Optional<DepartmentSpecialization> dsOpt = getDepartmentSpecializations()
                .stream()
                .filter(ds -> ds.getDepartment().equals(dep))
                .findFirst();

        if(dsOpt.isPresent()) {
            DepartmentSpecialization depSpec = dsOpt.get();
            studyFlow.setDepartmentSpecialization(depSpec);
            depSpec.getStudyFlows().add(studyFlow);
        } else {
            String msg = String.format(
                    "Unable to add study flow: current specialization (%s) is not belong to department %s.",
                    this.getCode(),
                    dep.getCipher()
            );
            throw new HibernateException(msg);
        }
    }

    @Override
    public String toString() {
        return "Specialization{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Specialization that = (Specialization) o;
        return id == that.id &&
                Objects.equals(code, that.code) &&
                Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, title);
    }
}
