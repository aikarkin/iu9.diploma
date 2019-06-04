package ru.bmstu.schedule.smtgen.model;

import com.microsoft.z3.*;
import ru.bmstu.schedule.smtgen.DayOfWeak;
import ru.bmstu.schedule.smtgen.LessonKind;
import ru.bmstu.schedule.smtgen.SubjectsPerWeek;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SmtScheduleModelGenerator {

    private Map<Integer, SubjectsPerWeek> totalSubjectsPerWeak;
    private Context ctx;
    private ScheduleSorts sorts;
    private ScheduleFunctions func;
    private ScheduleAsserts asserts;

    private List<TutorForLesson> tutorForLessons;
    private List<Integer> rooms;
    private List<Integer> groups;

    private Expr[] subjectsConsts;
    private Expr[] tutorsConsts;
    private Expr[] roomsConsts;
    private Expr[] groupsConsts;

    public SmtScheduleModelGenerator(
            Map<Integer, SubjectsPerWeek> totalSubjectsPerWeak,
            List<TutorForLesson> tutorForLessons,
            List<Integer> rooms,
            List<Integer> groups) {
        this.totalSubjectsPerWeak = totalSubjectsPerWeak;
        this.tutorForLessons = tutorForLessons;
        this.rooms = rooms;
        this.groups = groups;
        this.ctx = new Context();
        this.sorts = new ScheduleSorts(ctx);
        this.func = new ScheduleFunctions(sorts);
        this.asserts = new ScheduleAsserts(sorts, func);
    }

    public Optional<Model> createSmtModel() {
        subjectsConsts = createSubjectConstants();
        roomsConsts = createRoomConsts();
        groupsConsts = createGroupConstants();
        tutorsConsts = createTutorsConstants();

        Solver solver = ctx.mkSolver();
        solver.add(validSchedule());

        if(solver.check() == Status.SATISFIABLE) {
            return Optional.of(solver.getModel());
        }

        return Optional.empty();
    }

    private Expr[] createTutorsConstants() {
        int count = tutorForLessons.size();
        Expr[] constants = new Expr[count];

        for (int i = 0; i < count; i++) {
            TutorForLesson lesson = tutorForLessons.get(i);
            constants[i] = ctx.mkApp(sorts.tutorDecl(), ctx.mkInt(lesson.getTutorId()), sorts.kind(lesson.getKind()));
        }

        return constants;
    }

    private RealExpr subjCountToReal(double count) {
        double trunc = Math.floor(count);

        if (trunc == count) {
            return ctx.mkReal((int) count);
        } else {
            return (RealExpr) ctx.mkAdd(ctx.mkReal((int) trunc), ctx.mkReal(1, 2));
        }
    }

    private Expr[] createRoomConsts() {
        Expr[] roomConsts = new Expr[rooms.size()];
        int i = 0;

        for (int rId : rooms) {
            roomConsts[i++] = ctx.mkApp(sorts.roomDecl(), ctx.mkInt(rId));
        }

        return roomConsts;
    }

    private Expr[] createSubjectConstants() {
        int n = totalSubjectsPerWeak.keySet().size(), i = 0;
        Expr[] subjectsConstants = new Expr[n];

        for (int subjId : totalSubjectsPerWeak.keySet()) {
            subjectsConstants[i++] = ctx.mkApp(sorts.subjectDecl(), ctx.mkInt(subjId));
        }

        return subjectsConstants;
    }

    private Expr[] createGroupConstants() {
        int n = groups.size();
        Expr[] groupsConstants = new Expr[n];

        for (int i = 0; i < n; i++) {
            groupsConstants[i] = ctx.mkApp(sorts.groupDecl(), ctx.mkInt(groups.get(i)));
        }

        return groupsConstants;
    }

    private BoolExpr exprIsOneOf(Expr expr, Expr[] constants) {
        BoolExpr[] validExpr = new BoolExpr[constants.length];

        for (int i = 0; i < validExpr.length; i++) {
            validExpr[i] = ctx.mkEq(expr, constants[i]);
        }

        return ctx.mkOr(validExpr);
    }

    private BoolExpr validTutorForSubject(Expr tutor, Expr subj) {
        int i = 0;
        BoolExpr[] validTutorForSubj = new BoolExpr[tutorForLessons.size()];

        for (TutorForLesson tutorForLesson : tutorForLessons) {
            validTutorForSubj[i++] = ctx.mkAnd(
                    ctx.mkEq(sorts.subjId(subj), ctx.mkInt(tutorForLesson.getSubjectId())),
                    ctx.mkEq(sorts.tutorId(tutor), ctx.mkInt(tutorForLesson.getTutorId())),
                    ctx.mkEq(sorts.tutorLessonKind(tutor), sorts.kind(tutorForLesson.getKind()))
            );
        }

        return ctx.mkOr(validTutorForSubj);
    }

    private BoolExpr validNotEmptyLesson(Expr lesson) {
        return ctx.mkAnd(
                exprIsOneOf(sorts.lessonSubject(lesson), subjectsConsts),
                exprIsOneOf(sorts.lessonRoom(lesson), roomsConsts),
                validTutorForSubject(sorts.lessonTutor(lesson), sorts.lessonSubject(lesson))
        );
    }

    private BoolExpr validSlotForGroupAndDay(Expr group, Expr day, Expr slot) {
        Expr slotItem = ctx.mkApp(func.schedule(), group, day, slot);
        return ctx.mkOr(
                ctx.mkImplies(
                        ctx.mkAnd(
                                sorts.isSingleItemExpr(slotItem),
                                sorts.isNotBlankLessonExpr(sorts.singleItemLesson(slotItem))
                        ),
                        validNotEmptyLesson(sorts.singleItemLesson(slotItem))
                ),
                ctx.mkImplies(
                        ctx.mkAnd(
                                sorts.isPairItemExpr(slotItem),
                                sorts.hasNotEmptyNumerator(slotItem)
                        ),
                        validNotEmptyLesson(sorts.pairItemNumerator(slotItem))
                ),
                ctx.mkImplies(
                        ctx.mkAnd(
                                sorts.isPairItemExpr(slotItem),
                                sorts.hasNotEmptyDenominator(slotItem)
                        ),
                        validNotEmptyLesson(sorts.pairItemDenominator(slotItem))
                )
        );
    }

    private BoolExpr validLessonsForGroupInDay(Expr group, Expr day) {
        LessonSlot[] slots = LessonSlot.values();
        int n = slots.length;
        BoolExpr[] validLessonsInSlot = new BoolExpr[n];

        for (int i = 0; i < n; i++) {
            validLessonsInSlot[i] = validSlotForGroupAndDay(group, day, sorts.slot(slots[i]));
        }

        return ctx.mkAnd(validLessonsInSlot);
    }

    private BoolExpr validLessonsInWeakForGroup(Expr group) {
        DayOfWeak[] days = DayOfWeak.values();
        int n = days.length;
        BoolExpr[] validDaysForGroup = new BoolExpr[n];

        for (int i = 0; i < n; i++) {
            validDaysForGroup[i] = validLessonsForGroupInDay(group, sorts.dayOfWeak(days[i]));
        }

        return ctx.mkAnd(validDaysForGroup);
    }

    private BoolExpr validSubjectsCountOfEachKind(Expr group) {
        int n = subjectsConsts.length, i = 0;
        BoolExpr[] validTotalSubj = new BoolExpr[n];

        for (int subjId : totalSubjectsPerWeak.keySet()) {
            RealExpr lecCount, semCount, labCount;
            Expr subjExpr = subjectsConsts[i];
            SubjectsPerWeek lessonsPerWeek = this.totalSubjectsPerWeak.get(subjId);
            lecCount = subjCountToReal(lessonsPerWeek.getOrDefault(LessonKind.lec, 0.0));
            semCount = subjCountToReal(lessonsPerWeek.getOrDefault(LessonKind.sem, 0.0));
            labCount = subjCountToReal(lessonsPerWeek.getOrDefault(LessonKind.lab, 0.0));

            validTotalSubj[i] = asserts.validNumberOfSubjectsPerWeak(subjExpr, group, lecCount, semCount, labCount);
            i++;
        }

        return ctx.mkAnd(validTotalSubj);
    }

    private BoolExpr validScheduleForGroup(Expr group) {
        return ctx.mkAnd(
                asserts.validDaysInWeak(group),
                validSubjectsCountOfEachKind(group),
                validLessonsInWeakForGroup(group)
        );
    }


    private BoolExpr validSchedule() {
        int totalGroups = groups.size(), k = 0;
        BoolExpr[] validForTwoGroups = new BoolExpr[totalGroups * (totalGroups - 1) / 2];
        BoolExpr[] validScheduleForGroup = new BoolExpr[totalGroups];

        for (int i = 0; i < totalGroups; i++) {
            for (int j = i + 1; j < totalGroups; j++) {
                validForTwoGroups[k++] = asserts.validWeeksForTwoGroups(groupsConsts[i], groupsConsts[j]);
            }
            validScheduleForGroup[i] = validScheduleForGroup(groupsConsts[i]);
        }

        return ctx.mkAnd(
                ctx.mkAnd(validForTwoGroups),
                ctx.mkAnd(validScheduleForGroup)
        );
    }

}
