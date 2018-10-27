package ru.bmstu.schedule.html.node;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import ru.bmstu.schedule.html.FacultyDeserializer;
import ru.bmstu.schedule.html.commons.RootNode;

public class FacultiesListNode extends RootNode<FacultyNode> {

    public FacultiesListNode(Document document) {
        this.parseChildren(document.select(".container > .list-group > a.list-group-item"));
    }

    @Override
    public void parseChildren(Elements elements) {
        super.parseChildren(FacultyDeserializer.class, elements);
    }
}
