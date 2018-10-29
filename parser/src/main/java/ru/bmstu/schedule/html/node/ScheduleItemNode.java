package ru.bmstu.schedule.html.node;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.bmstu.schedule.html.commons.BasicNode;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ScheduleItemNode extends BasicNode<ScheduleDayNode, ScheduleItemParityNode> {
    private Time startsAt;
    private Time endsAt;

    public ScheduleItemNode(Time startsAt, Time endsAt) {
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public Time getStartsAt() {
        return startsAt;
    }

    public Time getEndsAt() {
        return endsAt;
    }

    @Override
    public void parseChildren(Elements elements) {
        if(elements.size() > 1) {
            ScheduleItemParityNode firstParity = parseItemParity(elements.get(1));
            if(elements.size() > 2) {
                ScheduleItemParityNode secondParity = parseItemParity(elements.get(2));

                if(firstParity != null) {
                    firstParity.setDayParity(ScheduleItemParityNode.DayParity.NUMERATOR);
                    this.getChildren().add(firstParity);
                }
                if(secondParity != null) {
                    secondParity.setDayParity(ScheduleItemParityNode.DayParity.DENUMERATOR);
                    this.getChildren().add(secondParity);
                }
            } else if(firstParity != null) {
                firstParity.setDayParity(ScheduleItemParityNode.DayParity.ANY);
                getChildren().add(firstParity);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleItemNode that = (ScheduleItemNode) o;
        return Objects.equals(startsAt, that.startsAt) &&
                Objects.equals(endsAt, that.endsAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startsAt, endsAt);
    }

    private static ScheduleItemParityNode parseItemParity(Element element) {
        if(element.children().size() > 0) {

            Elements iElem = element.select("i");
            Element spanElem = element.selectFirst("span");

            String classTypeAbbr = iElem.size() > 0 ? iElem.get(0).text() : null;
            String classroom = iElem.size() > 1 ? iElem.get(1).text().split(",")[0] : null;
            String lecturer  = iElem.size() > 2 ? iElem.get(2).text() : null;


            String subject = spanElem == null ? null : spanElem.text();

            String classType = null;
            if (classTypeAbbr != null && classTypeAbbr.length() > 0) {
                classType = CTYPE_ABBR_TO_NAME.get(classTypeAbbr.trim());
            }

            return new ScheduleItemParityNode(subject, classType, classroom, lecturer);
        }

        return null;
    }

    private static Map<String, String> CTYPE_ABBR_TO_NAME = new HashMap<>();
    static {
        CTYPE_ABBR_TO_NAME.put("(сем)", "семинар");
        CTYPE_ABBR_TO_NAME.put("(лек)", "лекция");
        CTYPE_ABBR_TO_NAME.put("(лаб)", "лабараторная работа");
    }
}
