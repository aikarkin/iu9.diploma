package ru.bmstu.schedule.parser.node;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import ru.bmstu.schedule.parser.ScheduleDayDeserializer;
import ru.bmstu.schedule.parser.commons.RootNode;
import ru.bmstu.schedule.parser.selector.ScheduleSelector;

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
