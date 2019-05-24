package ru.bmstu.schedule.html;

import org.jsoup.nodes.Element;
import ru.bmstu.schedule.html.commons.ElementDeserializer;
import ru.bmstu.schedule.html.node.ScheduleDayNode;
import ru.bmstu.schedule.html.selector.ScheduleDaySelector;

import java.util.HashMap;
import java.util.Map;

public class ScheduleDayDeserializer extends ElementDeserializer<ScheduleDayNode> {
    public static final Map<String, ScheduleDayNode.DayOfWeek> STR_TO_WEAK_DAY;
    static {
        STR_TO_WEAK_DAY = new HashMap<>();
        STR_TO_WEAK_DAY.put("ПН", ScheduleDayNode.DayOfWeek.MON);
        STR_TO_WEAK_DAY.put("ВТ", ScheduleDayNode.DayOfWeek.TUES);
        STR_TO_WEAK_DAY.put("СР", ScheduleDayNode.DayOfWeek.WED);
        STR_TO_WEAK_DAY.put("ЧТ", ScheduleDayNode.DayOfWeek.THU);
        STR_TO_WEAK_DAY.put("ПТ", ScheduleDayNode.DayOfWeek.FRI);
        STR_TO_WEAK_DAY.put("СБ", ScheduleDayNode.DayOfWeek.SAT);
    }

    public ScheduleDayDeserializer(Element element) {
        super(element);
    }

    @Override
    public ScheduleDayNode deserialize() {
        String dayOfWeakStr = elementHolder().getText(ScheduleDaySelector.dayOfWeak);

        ScheduleDayNode scheduleDay = new ScheduleDayNode(parseDayOfWeak(dayOfWeakStr));
        scheduleDay.parseChildren(elementHolder().getElements(ScheduleDaySelector.scheduleItems));

        return scheduleDay;
    }

    private static ScheduleDayNode.DayOfWeek parseDayOfWeak(String dayOfWeak) {
        if(STR_TO_WEAK_DAY.containsKey(dayOfWeak))
            return STR_TO_WEAK_DAY.get(dayOfWeak);

        return null;
    }
}
