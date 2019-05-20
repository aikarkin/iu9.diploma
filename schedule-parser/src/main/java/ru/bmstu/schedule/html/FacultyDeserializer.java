package ru.bmstu.schedule.html;


import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.bmstu.schedule.html.commons.ElementDeserializer;
import ru.bmstu.schedule.html.commons.ElementHolder;
import ru.bmstu.schedule.html.node.FacultyNode;
import ru.bmstu.schedule.html.selector.FacultySelector;

public class FacultyDeserializer extends ElementDeserializer<FacultyNode> {

    public FacultyDeserializer(Element element) {
        super(element);
    }

    @Override
    public FacultyNode deserialize() {
        String cipher = elementHolder().getText(FacultySelector.cipher);
        String name = elementHolder().getText(FacultySelector.name);

        FacultyNode faculty =  new FacultyNode(name, cipher);

        ElementHolder sibling = new ElementHolder(elementHolder().getSibling());
        Elements depElems = sibling.getElements(FacultySelector.departments);

        faculty.parseChildren(depElems);

        return faculty;
    }
}
