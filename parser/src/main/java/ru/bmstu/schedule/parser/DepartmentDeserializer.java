package ru.bmstu.schedule.parser;

import org.jsoup.nodes.Element;
import ru.bmstu.schedule.parser.commons.ElementDeserializer;
import ru.bmstu.schedule.parser.node.DepartmentNode;
import ru.bmstu.schedule.parser.selector.DepartmentSelector;

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

    public static DepartmentNode parseDepartmentFromCipher(String cipher) {
        Pattern defPtr = Pattern.compile("(\\p{L}+)(\\d+)"),
            sdPtr = Pattern.compile("\\p{L}+");
        Matcher defMatcher = defPtr.matcher(cipher),
            sdMatcher = sdPtr.matcher(cipher);

        String facultyCipher;
        int depNumber = -1;

        if(defMatcher.matches() && defMatcher.groupCount() == 2) {
            facultyCipher = defMatcher.group(1);
            try {
                depNumber = Integer.parseInt(defMatcher.group(2));
            } catch (NumberFormatException ignored) { }

            return new DepartmentNode(facultyCipher, depNumber);
        } else if(sdMatcher.matches()) {
            facultyCipher = sdMatcher.group();
            depNumber = 0;

            return new DepartmentNode(facultyCipher, depNumber);
        }

        return null;
    }
}
