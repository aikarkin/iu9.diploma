package ru.bmstu.schedule.html;

import org.jsoup.nodes.Element;
import ru.bmstu.schedule.html.commons.ElementDeserializer;
import ru.bmstu.schedule.html.node.ScheduleItemNode;
import ru.bmstu.schedule.html.selector.ScheduleDaySelector;

import java.sql.Time;

public class ScheduleItemDeserializer extends ElementDeserializer<ScheduleItemNode> {

    public ScheduleItemDeserializer(Element element) {
        super(element);
    }

    @Override
    public ScheduleItemNode deserialize() {
        String timeStr = elementHolder().getText(ScheduleDaySelector.time);
        ScheduleItemNode scheduleItem = parseClassTime(timeStr);

        if(scheduleItem != null)
            scheduleItem.parseChildren(elementHolder().getElements(ScheduleDaySelector.paritiesItems)); // !!UNCHECKED

        return scheduleItem;
    }

    public static ScheduleItemNode parseClassTime(String timeStr) {
        String[] dash = timeStr.split("-");

        if(dash.length > 1) {
            String startsAtStr = dash[0], endsAtStr = dash[1];
            Time startsAt = parseTime(startsAtStr);
            Time endsAt = parseTime(endsAtStr);

            return new ScheduleItemNode(startsAt, endsAt);
        }

        return null;
    }

    private static Time parseTime(String timeStr) {
        String[] colon = timeStr.trim().split(":");
        String hoursStr = colon.length > 0 ? colon[0] : "",
                minutesStr = colon.length > 1 ? colon[1] : "0",
                secondsHours = colon.length > 2 ? colon[2] : "0";

        int hours = Integer.parseInt(hoursStr),
                mins = Integer.parseInt(minutesStr),
                secs = Integer.parseInt(secondsHours);

        return new Time(hours, mins, secs);
    }
}
