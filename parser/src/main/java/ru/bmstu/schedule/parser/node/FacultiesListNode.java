package ru.bmstu.schedule.parser.node;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import ru.bmstu.schedule.parser.FacultyDeserializer;
import ru.bmstu.schedule.parser.commons.RootNode;

public class FacultiesListNode extends RootNode<FacultyNode> {

    public FacultiesListNode(Document document) {
        this.parseChildren(document.select(".container > .list-group > a.list-group-item"));
    }

    @Override
    public void parseChildren(Elements elements) {
        super.parseChildren(FacultyDeserializer.class, elements);
    }
}
