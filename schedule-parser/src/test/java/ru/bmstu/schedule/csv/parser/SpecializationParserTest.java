package ru.bmstu.schedule.csv.parser;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpecializationParserTest {

    private static final Pattern YEARS_PTR = Pattern.compile("(\\d+)г\\.(\\s(\\d+)м\\.)?");

    @Test
    public void testYearsPattern() {
        String sample1 = "4г.";
        String sample2 = "4г. 3м.";

        Matcher matcher1 = YEARS_PTR.matcher(sample1);
        Matcher matcher2 = YEARS_PTR.matcher(sample2);

        assertTrue(matcher1.matches());
        assertTrue(matcher2.matches());

        assertEquals("4", matcher1.group(1));
        assertEquals("3", matcher2.group(3));
    }

}