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
    private Map<Integer, Integer> tutor2Subjects;
    private List<Integer> rooms;
    private List<Integer> groups;

    private Expr[] subjectsExprs;
    private Expr[] roomsExprs;
    private Expr[] groupsExprs;

    public SmtScheduleModelGenerator(
            Map<Integer, SubjectsPerWeek> totalSubjectsPerWeak,
            Map<Integer, Integer> tutor2Subjects,
            List<Integer> rooms,
            List<Integer> groups) {
        this.totalSubjectsPerWeak = totalSubjectsPerWeak;
        this.tutor2Subjects = tutor2Subjects;
        this.rooms = rooms;
        this.groups = groups;
        this.ctx = new Context();
        this.sorts = new ScheduleSorts(ctx);
        this.func = new ScheduleFunctions(sorts);
        this.asserts = new ScheduleAsserts(sorts, func);
    }

    public Optional<Model> createSmtModel() {
        subjectsExprs = createSubjectExpressions();
        roomsExprs = createRoomExpressions();
        groupsExprs = createGroupExpressions();

        Solver solver = ctx.mkSolver();
        solver.add(validSchedule());

        if(solver.check() == Status.SATISFIABLE) {
            return Optional.of(solver.getModel());
        }

        return Optional.empty();
    }

    private RealExpr subjCountToReal(double count) {
        double trunc = Math.floor(count);

        if (trunc == count) {
            return ctx.mkReal((int) count);
        } else {
            return (RealExpr) ctx.mkAdd(ctx.mkReal((int) trunc), ctx.mkReal(1, 2));
        }
    }

    private Expr[] createRoomExpressions() {
        Expr[] roomConsts = new Expr[rooms.size()];
        int i = 0;

        for (int rId : rooms) {
            roomConsts[i++] = ctx.mkApp(sorts.roomDecl(), ctx.mkInt(rId));
        }

        return roomConsts;
    }

    private Expr[] createSubjectExpressions() {
        int n = totalSubjectsPerWeak.keySet().size(), i = 0;
        Expr[] subjectsConstants = new Expr[n];

        for (int subjId : totalSubjectsPerWeak.keySet()) {
            subjectsConstants[i++] = ctx.mkApp(sorts.subjectDecl(), ctx.mkInt(subjId));
        }

        return subjectsConstants;
    }

    private Expr[] createGroupExpressions() {
        int n = groups.size();
        Expr[] groupsConstants = new Expr[n];

        for (int i = 0; i < n; i++) {
            groupsConstants[i] = ctx.mkApp(sorts.groupDecl(), ctx.mkInt(groups.get(i)));
        }

        return groupsConstants;
    }

    private BoolExpr exprIsOnOf(Expr expr, Expr[] constants) {
        BoolExpr[] validExpr = new BoolExpr[constants.length];

        for (int i = 0; i < validExpr.length; i++) {
            validExpr[i] = ctx.mkEq(expr, constants[i]);
        }

        return ctx.mkOr(validExpr);
    }

    private BoolExpr validTutorForSubject(Expr tutor, Expr subj) {
        int i = 0;
        BoolExpr[] validTutorForSubj = new BoolExpr[tutor2Subjects.size()];

        for (Map.Entry<Integer, Integer> tutorAndSubj : tutor2Subjects.entrySet()) {
            validTutorForSubj[i++] = ctx.mkAnd(
                    ctx.mkEq(sorts.tutorId(tutor), ctx.mkInt(tutorAndSubj.getKey())),
                    ctx.mkEq(sorts.subjId(subj), ctx.mkInt(tutorAndSubj.getValue()))
            );
        }

        return ctx.mkOr(validTutorForSubj);
    }

    private BoolExpr validNotEmptyLesson(Expr lesson) {
        return ctx.mkAnd(
                exprIsOnOf(sorts.lessonSubject(lesson), subjectsExprs),
                exprIsOnOf(sorts.lessonRoom(lesson), roomsExprs),
                validTutorForSubject(sorts.lessonTutor(lesson), sorts.lessonSubject(lesson))
        );
    }

    private BoolExpr validSlotForGroupAndDay(Expr group, Expr day, Expr slot) {
        Expr slotItem = ctx.mkApp(func.schedule(), group, day, slot);
        return ctx.mkOr(
                ctx.mkImplies(
                        ctx.mkAnd(
                                sorts.isSingleItemExpr(slotItem),
                                sorts.isNotBlankLessonExpr(slotItem)
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
        int n = subjectsExprs.length, i = 0;
        BoolExpr[] validTotalSubj = new BoolExpr[n];

        for (int subjId : totalSubjectsPerWeak.keySet()) {
            RealExpr lecCount, semCount, labCount;
            Expr subjExpr = subjectsExprs[i];
            SubjectsPerWeek lessonsPerWeek = this.totalSubjectsPerWeak.get(subjId);
            lecCount = subjCountToReal(lessonsPerWeek.get(LessonKind.lec));
            semCount = subjCountToReal(lessonsPerWeek.get(LessonKind.sem));
            labCount = subjCountToReal(lessonsPerWeek.get(LessonKind.lab));

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
                validForTwoGroups[k++] = asserts.validWeeksForTwoGroups(groupsExprs[i], groupsExprs[j]);
            }
            validScheduleForGroup[i] = validScheduleForGroup(groupsExprs[i]);
        }

        return ctx.mkAnd(
                ctx.mkAnd(validForTwoGroups),
                ctx.mkAnd(validScheduleForGroup)
        );
    }

}
