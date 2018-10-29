package ru.bmstu.schedule.entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "schedule_day", schema = "public", catalog = "schedule")
public class ScheduleDay {
    private int id;
    private DayOfWeak dayOfWeak;
    private StudyGroup studyGroup;
    private Collection<ScheduleItem> scheduleItems;

    @Id
    @Column(name = "day_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleDay that = (ScheduleDay) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @ManyToOne
    @JoinColumn(name = "weak_id", referencedColumnName = "weak_id")
    public DayOfWeak getDayOfWeak() {
        return dayOfWeak;
    }

    public void setDayOfWeak(DayOfWeak dayOfWeak) {
        this.dayOfWeak = dayOfWeak;
    }

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "group_id")
    public StudyGroup getStudyGroup() {
        return studyGroup;
    }

    public void setStudyGroup(StudyGroup studyGroup) {
        this.studyGroup = studyGroup;
    }

    @OneToMany(mappedBy = "scheduleDay", cascade = CascadeType.ALL)
    public Collection<ScheduleItem> getScheduleItems() {
        return scheduleItems;
    }

    public void setScheduleItems(Collection<ScheduleItem> scheduleItems) {
        this.scheduleItems = scheduleItems;
    }

    public void addScheduleItem(ScheduleItem item) {
        item.setScheduleDay(this);
        getScheduleItems().add(item);
    }
}
