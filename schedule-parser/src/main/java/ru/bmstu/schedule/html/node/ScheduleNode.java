package ru.bmstu.schedule.html.node;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import ru.bmstu.schedule.html.selector.ScheduleSelector;
import ru.bmstu.schedule.html.ScheduleDayDeserializer;
import ru.bmstu.schedule.html.commons.RootNode;

public class ScheduleNode extends RootNode<ScheduleDayNode> {
    public ScheduleNode(Document doc) {
        Elements elements = doc.select(ScheduleSelector.days.getQuery());
        this.parseChildren(elements);
    }

    @Override
    public void parseChildren(Elements elements) {
        parseChildren(ScheduleDayDeserializer.class, elements);
    }
}
