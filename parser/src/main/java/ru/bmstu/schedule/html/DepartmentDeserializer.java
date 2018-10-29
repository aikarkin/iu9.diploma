package ru.bmstu.schedule.html;

import org.jsoup.nodes.Element;
import ru.bmstu.schedule.html.commons.ElementDeserializer;
import ru.bmstu.schedule.html.node.DepartmentNode;
import ru.bmstu.schedule.html.selector.DepartmentSelector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DepartmentDeserializer extends ElementDeserializer<DepartmentNode> {

    public DepartmentDeserializer(Element element) {
        super(element);
    }

    @Override
    public DepartmentNode deserialize() {
        String deptCipher = elementHolder().getText(DepartmentSelector.name);
        DepartmentNode dept = parseDepartmentFromCipher(deptCipher);
        if(dept != null) {
            dept.parseChildren(elementHolder().getElements(DepartmentSelector.courses));
            return dept;
        }

        return null;
    }

    public static DepartmentNode parseDepartmentFromCipher(String depCode) {
        Pattern defPtr = Pattern.compile("(\\p{L}+)(\\d+)"),
            sdPtr = Pattern.compile("\\p{L}+");
        Matcher defMatcher = defPtr.matcher(depCode),
            sdMatcher = sdPtr.matcher(depCode);

        String facultyCipher;
        int depNumber = -1;

        if(defMatcher.matches() && defMatcher.groupCount() > 1) {
            facultyCipher = defMatcher.group(1);

            try {
                depNumber = Integer.parseInt(defMatcher.group(2));
            } catch (NumberFormatException ignored) { }

            return new DepartmentNode(defMatcher.group(), facultyCipher, depNumber);
        } else if(sdMatcher.matches()) {
            facultyCipher = sdMatcher.group();
            depNumber = 0;

            return new DepartmentNode(sdMatcher.group(), facultyCipher, depNumber);
        }

        return null;
    }
}
