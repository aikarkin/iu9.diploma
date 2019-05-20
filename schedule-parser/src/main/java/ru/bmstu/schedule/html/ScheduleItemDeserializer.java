package ru.bmstu.schedule.html;

import org.jsoup.nodes.Element;
import ru.bmstu.schedule.html.commons.ElementDeserializer;
import ru.bmstu.schedule.html.node.ScheduleItemNode;
import ru.bmstu.schedule.html.selector.ScheduleDaySelector;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

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
//        String hoursStr = colon.length > 0 ? colon[0] : "",
//                minutesStr = colon.length > 1 ? colon[1] : "0",
//                secondsHours = colon.length > 2 ? colon[2] : "0";
//
//        int hours = Integer.parseInt(addThoursStr),
//                mins = Integer.parseInt(addTminutesStr),
//                secs = Integer.parseInt(addTsecondsHours);
//
//        System.out.println("time = [" + hours + ":" + mins + ":" + secs + "]");

//        System.out.println(Time.valueOf(timeStr.trim() + ":00"));

        return Time.valueOf(timeStr.trim() + ":00");
    }

    public String addTimeZeros(String timeUnit) {
        if(timeUnit.length() == 1)
            return "0" + timeUnit;

        return timeUnit;
    }
}
