package ru.bmstu.schedule.html;

import org.jsoup.nodes.Element;
import ru.bmstu.schedule.html.commons.ElementDeserializer;
import ru.bmstu.schedule.html.node.GroupNode;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupDeserializer extends ElementDeserializer<GroupNode> {
    private static final String DEFAULT_DEGREE = "С";
    private static final HashMap<String, GroupNode.Degree> DCODE_TO_DEGREE;
    static
    {
        DCODE_TO_DEGREE = new HashMap<>();
        DCODE_TO_DEGREE.put("Б", GroupNode.Degree.BACHELOR);
        DCODE_TO_DEGREE.put("М", GroupNode.Degree.MASTER);
        DCODE_TO_DEGREE.put("А", GroupNode.Degree.PHILOSOPHY);
        DCODE_TO_DEGREE.put("С", GroupNode.Degree.SPECIALTY);
    }

    public GroupDeserializer(Element element) {
        super(element);
    }

    @Override
    public GroupNode deserialize() {
        String groupCipher = elementHolder().getText();
        String groupLink = elementHolder().getAttribute("href");

        GroupNode group = parseGroupFromCipher(groupCipher);

        if(group != null)
            group.setScheduleLink(groupLink);

        return group;
    }

    public static GroupNode parseGroupFromCipher(String groupCipher) {
        Pattern defPtr = Pattern.compile("(\\p{L}+)(\\d+)-(\\d+)(\\p{L})?"),
                singleDepPtr = Pattern.compile("(\\p{L}+)-(\\d+)(\\p{L})?");
        Matcher defMatcher = defPtr.matcher(groupCipher),
                singleDepMatcher = singleDepPtr.matcher(groupCipher);



        String faculty, dept, code, degreeStr;

        if(defMatcher.lookingAt() && defMatcher.groupCount() > 3) {
            faculty = defMatcher.group(1);
            dept = defMatcher.group(2);
            code = defMatcher.group(3);
            degreeStr = defMatcher.group(4) != null ? defMatcher.group(4) : DEFAULT_DEGREE;
        } else if(singleDepMatcher.lookingAt() && singleDepMatcher.groupCount() > 2) {
                faculty = singleDepMatcher.group(1);
                dept = "0";
                code = singleDepMatcher.group(2);
                degreeStr = singleDepMatcher.group(3) != null ? singleDepMatcher.group(3) : DEFAULT_DEGREE;

        } else {
            return null;
        }

        int deptNo = -1, groupNo = -1, termNo = -1;

        try {
            deptNo = Integer.parseInt(dept);
            if(code.length() > 1) {
                String term = code.substring(0, code.length() - 1),
                        group = code.substring(code.length() - 1);
                termNo = Integer.parseInt(term);
                groupNo = Integer.parseInt(group);
            }
        } catch (NumberFormatException ignored) { }

        GroupNode.Degree degree = DCODE_TO_DEGREE.containsKey(degreeStr.toUpperCase())
                ? DCODE_TO_DEGREE.get(degreeStr)
                : GroupNode.Degree.BACHELOR;

        return new GroupNode(faculty, deptNo, termNo, groupNo, degree);
    }


}
