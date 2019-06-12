package ru.bmstu.schedule.smtgen.cli;

import org.apache.commons.cli.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.cli.Option.builder;

public class CommandLineParser {

    private static final String GROUP_RE = "(\\p{Lu}+)(\\d+)?-\\d{2,}";
    private static final String UTIL_NAME = "smtgen";
    private static final Map<String, String> STUDY_PLAN_PARAMS_RE;

    static {
        STUDY_PLAN_PARAMS_RE = new HashMap<>();
        STUDY_PLAN_PARAMS_RE.put("d", "(\\p{Lu}{2})(\\d+)?");
        STUDY_PLAN_PARAMS_RE.put("t", "\\d+");
        STUDY_PLAN_PARAMS_RE.put("y", "\\d{4}");
        STUDY_PLAN_PARAMS_RE.put("s", "\\d{1,2}\\.\\d{1,2}\\.\\d{1,2}_\\d{1,2}");
    }

    private CommandLine cmd;
    private HelpFormatter helpFormatter;
    private Options opts;

    public CommandLineParser() {
        opts = new Options();
        helpFormatter = new HelpFormatter();
        setOptions();
    }

    public ScheduleConfiguration parse(String[] args) throws ParseException {
        cmd = new DefaultParser().parse(opts, args);
        checkCmdArgs();
        ScheduleConfiguration config = new ScheduleConfiguration();

        if (cmd.hasOption("g")) {
            config.setGroupCiphers(getNonEmptyOptions("g"));
        } else {
            config.setDepartmentCipher(cmd.getOptionValue("d"));
            config.setSpecializationCode(cmd.getOptionValue("s"));
            config.setNoOfTerm(Integer.valueOf(cmd.getOptionValue("t")));
            config.setEnrollmentYear(Integer.valueOf(cmd.getOptionValue("y")));
        }

        return config;
    }

    public void printHelp() {
        helpFormatter.printHelp(UTIL_NAME, opts);
    }

    private void checkCmdArgs() throws ParseException {
        boolean paramsForStudyPlanProvided = STUDY_PLAN_PARAMS_RE.keySet().stream().allMatch(cmd::hasOption);
        if (!cmd.hasOption("g") && !paramsForStudyPlanProvided) {
            String spParams = STUDY_PLAN_PARAMS_RE
                    .keySet()
                    .stream()
                    .map(opt -> "'-" + opt + "'")
                    .collect(Collectors.joining(", "));
            String msg = String.format(
                    "Указаны не все параметры - требуется укзать либо параметр '-%s', либо параметры: %s",
                    "g",
                    spParams
            );

            throw new ParseException(msg);
        }

        if (cmd.hasOption("g")) {
            List<String> groupsCiphers = getNonEmptyOptions("g");

            if (groupsCiphers.size() == 0) {
                throw new ParseException("список групп пуст");
            }

            for (String cipher : groupsCiphers) {
                if (!cipher.matches(GROUP_RE)) {
                    throw new ParseException(String.format(
                            "Неправильный формат шифра группы: %s. Шифр группы должен указываться в формате: %s (%s)",
                            cipher,
                            "{шифр кафедры}-{номер семестра}{номер группы}",
                            "например, ИУ9-52 или ЮР-12"
                    ));
                }
            }
        } else {
            for (String opt : STUDY_PLAN_PARAMS_RE.keySet()) {
                String value = cmd.getOptionValue(opt);
                if (!value.matches(STUDY_PLAN_PARAMS_RE.get(opt))) {
                    throw new ParseException(String.format(
                            "Невалидное значаение параметра -%s: %s",
                            opt,
                            value
                    ));
                }
            }
        }

    }

    private void setOptions() {
        opts.addOption(builder("g")
                .longOpt("groups")
                .desc("Список шифров учебных групп")
                .hasArgs()
                .numberOfArgs(Option.UNLIMITED_VALUES)
                .valueSeparator(',')
                .build()
        );

        opts.addOption(builder("s")
                .longOpt("specialization")
                .desc("Код специализации")
                .numberOfArgs(1)
                .type(String.class)
                .build()
        );

        opts.addOption(builder("d")
                .longOpt("department")
                .desc("Шифр кафедры")
                .numberOfArgs(1)
                .type(String.class)
                .build()
        );

        opts.addOption(builder("t")
                .longOpt("term")
                .desc("Номер семестра")
                .numberOfArgs(1)
                .type(Integer.class)
                .build()
        );

        opts.addOption(builder("y")
                .longOpt("year")
                .desc("Год начала обучения")
                .numberOfArgs(1)
                .type(Integer.class)
                .build()
        );

        opts.addOption(builder("h")
                .longOpt("help")
                .desc("Выводит справочную информацию")
                .hasArg(false)
                .build()
        );
    }

    private List<String> getNonEmptyOptions(String opt) {
        return Arrays.stream(cmd.getOptionValues(opt))
                .filter(str -> !str.isEmpty())
                .collect(Collectors.toList());
    }

}
