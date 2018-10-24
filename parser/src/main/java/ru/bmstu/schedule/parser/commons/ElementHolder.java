package ru.bmstu.schedule.parser.commons;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.bmstu.schedule.parser.selector.QuerySelector;

public class ElementHolder {
    private Element elem;

    public ElementHolder(Element elem) {
        this.elem = elem;
    }

    public Elements getElements(QuerySelector query) {
        return elem.select(query.getQuery());
    }

    public Element getFirst(QuerySelector query) {
        Elements elements = this.getElements(query);
        return elements == null || elements.size() == 0 ? null : elements.first();
    }

    public Element getSibling() {
        return this.elem.nextElementSibling();
    }

    public String getText(QuerySelector query) {
        Element first = this.getFirst(query);
        return first == null ? null : first.text();
    }

    public String getText() {
        return elem.text();
    }

    public String getAttribute(String attrName) {
        return elem.attr(attrName);
    }

    public String getAttribute(QuerySelector query, String attrName) {
        Element first = this.getFirst(query);
        return first == null ? null : first.attr(attrName);
    }
}
