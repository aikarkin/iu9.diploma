package ru.bmstu.schedule.html.parser;

import com.sun.istack.internal.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.bmstu.schedule.html.commons.Node;
import ru.bmstu.schedule.html.commons.NodeTraveller;
import ru.bmstu.schedule.html.node.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleParser {
    static final String SCHEDULE_LIST_PATH = "/schedule/list/";
    static final String SCHEDULE_PATH = "/schedule/";
    private String baseurl;
    private Document scheduleListDoc;
    private Map<String, String> groupCipherToId;

    public ScheduleParser(String baseurl) throws IOException {
        this.baseurl = baseurl;
        scheduleListDoc = Jsoup.connect(baseurl + SCHEDULE_LIST_PATH).get();
    }

    public Document getScheduleListDoc() {
        return scheduleListDoc;
    }

    public List<FacultyNode> getFaculties() {
        return new FacultiesListNode(scheduleListDoc).getChildren();
    }

    public Iterable<DepartmentNode> getAllDepartments() {
        return entitiesOf(DepartmentNode.class);
    }

    public Iterable<GroupNode> getAllGroups() {
        return entitiesOf(GroupNode.class);
    }

    public Iterable<CourseNode> getAllCourses() {
        return entitiesOf(CourseNode.class);
    }

    public <T extends Node> Iterable<T> entitiesOf(Class<? extends T> clazz) {
        return new NodeTraveller(new FacultiesListNode(scheduleListDoc)).entitiesListOf(clazz);
    }

    public NodeTraveller getListTraveler() {
        return new NodeTraveller(new FacultiesListNode(scheduleListDoc));
    }

    public NodeTraveller scheduleTravellerFor(GroupNode group) throws IOException {
        Document doc = Jsoup.connect(baseurl + group.getScheduleLink()).get();
        return new NodeTraveller(new ScheduleNode(doc));
    }

    public List<ScheduleDayNode> scheduleFor(@NotNull GroupNode group) throws IllegalStateException, IOException {
        if (group.getUuid() == null) {
            String msg = String.format(
                    "Failed to fetch schedule for '%s'. Group 'link' property should not be null",
                    group.toString()
            );
            throw new IllegalStateException(msg);
        }
        return scheduleFor(group.getUuid());
    }

    public List<ScheduleDayNode> scheduleFor(@NotNull String groupId) throws IOException, IllegalArgumentException {
        if (groupCipherToId == null)
            buildCipherToIdMap();

        String link;
        if (GroupNode.isUuid(groupId)) {
            link = baseurl + SCHEDULE_PATH + groupId.trim();
        } else if (groupCipherToId.containsKey(groupId.trim())) {
            link = baseurl + SCHEDULE_PATH + groupCipherToId.get(groupId.trim());
        } else {
            throw new IllegalArgumentException("Invalid group id.");
        }

        Document doc = Jsoup.connect(link).get();

        return new ScheduleNode(doc).getChildren();
    }


    private void buildCipherToIdMap() {
        final HashMap<String, String> resMap = new HashMap<>();

        this.getFaculties()
                .stream()
                .flatMap(f -> f.getChildren().stream())
                .flatMap(d -> d.getChildren().stream())
                .flatMap(c -> c.getChildren().stream())
                .forEach(g -> resMap.put(g.getCipher(), g.getUuid()));

        groupCipherToId = resMap;
    }
}
