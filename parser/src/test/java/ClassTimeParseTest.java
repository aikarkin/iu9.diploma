import org.junit.Test;
import ru.bmstu.schedule.parser.ScheduleItemDeserializer;
import ru.bmstu.schedule.parser.node.ScheduleItemNode;

import java.sql.Time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ClassTimeParseTest {
    @Test
    public void testClassTimeParsing() {
        ScheduleItemNode scheduleItemNode = ScheduleItemDeserializer.parseClassTime("12:00 - 13:35");
        assertNotNull(scheduleItemNode);
        assertNotNull(scheduleItemNode.getStartsAt());
        assertNotNull(scheduleItemNode.getEndsAt());

        assertEquals(new Time(12, 0, 0), scheduleItemNode.getStartsAt());
        assertEquals(new Time(13, 35, 0), scheduleItemNode.getEndsAt());
    }

    private static Time strToTime(String str) {
        String[] colon = str.split(":");
        String hoursStr = colon.length > 0 ? colon[0] : "",
                minutesStr = colon.length > 1 ? colon[1] : "0",
                secondsHours = colon.length > 2 ? colon[2] : "0";

        int hours = Integer.parseInt(hoursStr),
                mins = Integer.parseInt(minutesStr),
                secs = Integer.parseInt(secondsHours);

        return new Time(hours, mins, secs);
    }
}
