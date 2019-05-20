package ru.bmstu.schedule.html;

import org.jsoup.nodes.Element;
import ru.bmstu.schedule.html.commons.ElementDeserializer;
import ru.bmstu.schedule.html.node.ScheduleDayNode;
import ru.bmstu.schedule.html.selector.ScheduleDaySelector;

import java.util.HashMap;
import java.util.Map;

public class ScheduleDayDeserializer extends ElementDeserializer<ScheduleDayNode> {
    public static final Map<String, ScheduleDayNode.DayOfWeak> STR_TO_WEAK_DAY;
    static {
        STR_TO_WEAK_DAY = new HashMap<>();
        STR_TO_WEAK_DAY.put("ПН", ScheduleDayNode.DayOfWeak.MON);
        STR_TO_WEAK_DAY.put("ВТ", ScheduleDayNode.DayOfWeak.TUES);
        STR_TO_WEAK_DAY.put("СР", ScheduleDayNode.DayOfWeak.WED);
        STR_TO_WEAK_DAY.put("ЧТ", ScheduleDayNode.DayOfWeak.THU);
        STR_TO_WEAK_DAY.put("ПТ", ScheduleDayNode.DayOfWeak.FRI);
        STR_TO_WEAK_DAY.put("СБ", ScheduleDayNode.DayOfWeak.SAT);
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

    private static ScheduleDayNode.DayOfWeak parseDayOfWeak(String dayOfWeak) {
        if(STR_TO_WEAK_DAY.containsKey(dayOfWeak))
            return STR_TO_WEAK_DAY.get(dayOfWeak);

        return null;
    }
}
