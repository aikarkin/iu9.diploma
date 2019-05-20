package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Faculty {
    private int id;
    private String cipher;
    private String title;
    private Set<Department> departments = new HashSet<>();

    @Override
    public String toString() {
        return "Faculty{" +
                "id=" + id +
                ", cipher='" + cipher + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    public Faculty(String cipher, String title) {
        this.cipher = cipher;
        this.title = title;
    }

    public Faculty() {

    }

    @Id
    @Column(name = "faculty_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "faculty_cipher", columnDefinition = "bpchar", length = 8, unique = true)
        public String getCipher() {
        return cipher.trim();
    }

    public void setCipher(String cipher) {
        this.cipher = cipher;
    }

    @Basic
    @Column(name = "title", nullable = false, length = -1)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    @OneToMany(mappedBy = "faculty", cascade = CascadeType.ALL)
    public Set<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(Set<Department> departments) {
        this.departments = departments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Faculty faculty = (Faculty) o;
        return id == faculty.id &&
                Objects.equals(cipher, faculty.cipher) &&
                Objects.equals(title, faculty.title);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, cipher, title);
    }

    public void addDepartment(Department department) {
        department.setFaculty(this);
        departments.add(department);
    }
}
