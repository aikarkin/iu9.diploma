package ru.bmstu.schedule.smtgen.cli;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CommandLineParserTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "-d фыва-43 -s 01.03.02_1 -y 1234",
            "-d ИУ9 -s 01.03.к2_13 -y 2014 -t 3",
            "-d ИУ9 -s 01.03.2_1 -y 214 -t 3",
            "-d ИУ9 -s 01.03.2_1 -y 2014 -t fd",
            "-g group1, group2",
            "-g ИУ9-21, ЮР, group2",
            "-g ,",
    })
    public void testInvalidParameters(String opts) {
        System.out.println("options: " + opts);
        String[] args = opts.split("\\s+");
        CommandLineParser parser = new CommandLineParser();
        assertThrows(ParseException.class, () -> parser.parse(args));
    }

    @ParameterizedTest
    @CsvSource({
            "-d ИУ9 -s 01.03.02_1 -y 2015 -t 2, ИУ9, 01.03.02_1, 2015, 2",
            "-d ЮР -s 01.12.04_12 -y 2018 -t 5, ЮР, 01.12.04_12, 2018, 5"
    })
    public void testValidStudyPlan(String opts, String dept, String spec, int year, int term) throws ParseException {
        String[] args = opts.split("\\s+");
        CommandLineParser parser = new CommandLineParser();
        ScheduleConfiguration config = parser.parse(args);

        assertEquals(dept, config.getDepartmentCipher());
        assertEquals(spec, config.getSpecializationCode());
        assertEquals(year, config.getEnrollmentYear());
        assertEquals(term, config.getNoOfTerm());
        assertNull(config.getGroupCiphers());
    }

    @ParameterizedTest
    @MethodSource("groupsSource")
    void testValidGroups(String opts, List<String> ciphers) throws ParseException {
        String[] args = opts.split("\\s+");
        CommandLineParser parser = new CommandLineParser();
        ScheduleConfiguration config = parser.parse(args);

        assertEquals(ciphers, config.getGroupCiphers());

        assertEquals(config.getEnrollmentYear(), -1);
        assertEquals(config.getNoOfTerm(), -1);
        assertNull(config.getDepartmentCipher());
        assertNull(config.getSpecializationCode());
    }

    private static Stream<Arguments> groupsSource() {
        return Stream.of(
                Arguments.of("-g ИУ9-54, ИУ9-12, ИБМ-112,  Э9-32", Arrays.asList("ИУ9-54","ИУ9-12", "ИБМ-112", "Э9-32")),
                Arguments.of("-g ИУ9-21", Collections.singletonList("ИУ9-21")),
                Arguments.of("-g ЮР-12", Collections.singletonList("ЮР-12"))
        );
    }
}