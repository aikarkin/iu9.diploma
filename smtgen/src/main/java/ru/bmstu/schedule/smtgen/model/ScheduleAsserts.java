package ru.bmstu.schedule.smtgen.model;

import com.microsoft.z3.*;
import ru.bmstu.schedule.smtgen.DayOfWeak;
import ru.bmstu.schedule.smtgen.LessonKind;

public class ScheduleAsserts {

    private static final int MIN_LESSONS_PER_DAY = 2;
    private static final int MAX_LESSONS_PER_DAY = 5;

    private ScheduleSorts sorts;
    private ScheduleFunctions func;

    private Context ctx;

    public ScheduleAsserts(ScheduleSorts sorts, ScheduleFunctions scheduleFunctions) {
        this.sorts = sorts;
        this.ctx = sorts.getContext();
        this.func = scheduleFunctions;
    }

    public BoolExpr validDaysInWeak(Expr group) throws IllegalArgumentException {
        checkExprsSort(sorts.group(), group);
        DayOfWeak[] days = DayOfWeak.values();
        BoolExpr[] validDays = new BoolExpr[days.length];

        for (int i = 0; i < days.length; i++) {
            validDays[i] = validSlotsInDay(group, sorts.dayOfWeak(days[i]));
        }

        return ctx.mkAnd(validDays);
    }

    public BoolExpr validWeeksForTwoGroups(Expr group1, Expr group2) {
        checkExprsSort(sorts.group(), group1, group2);
        DayOfWeak[] days = DayOfWeak.values();
        BoolExpr[] validDays = new BoolExpr[days.length];

        for (int i = 0; i < days.length; i++) {
            validDays[i] = validDayForTwoGroups(group1, group2, sorts.dayOfWeak(days[i]));
        }

        return ctx.mkAnd(validDays);
    }

    public BoolExpr validNumberOfSubjectsPerWeak(Expr subj, Expr group, RealExpr lec, RealExpr sem, RealExpr lab) {
        checkExprsSort(sorts.subject(), subj);
        checkExprsSort(sorts.group(), group);

        return ctx.mkAnd(
                ctx.mkEq(lec, countLessonsInWeak(subj, group, sorts.kind(LessonKind.lec))),
                ctx.mkEq(sem, countLessonsInWeak(subj, group, sorts.kind(LessonKind.sem))),
                ctx.mkEq(lab, countLessonsInWeak(subj, group, sorts.kind(LessonKind.lab)))
        );
    }

    private RealExpr countLessonsInWeak(Expr subject, Expr group, Expr kind) {
        DayOfWeak[] days = DayOfWeak.values();
        RealExpr[] countDayLessons = new RealExpr[days.length];

        for (int i = 0; i < countDayLessons.length; i++) {
            countDayLessons[i] = countLessonsInDay(subject, group, kind, sorts.dayOfWeak(days[i]));
        }

        return (RealExpr) ctx.mkAdd(countDayLessons);
    }

    private RealExpr countLessonsInDay(Expr subject, Expr group, Expr kind, Expr day) {
        LessonSlot[] lessonSlots = LessonSlot.values();
        RealExpr[] countSlotLessons = new RealExpr[lessonSlots.length];

        for (int i = 0; i < countSlotLessons.length; i++) {
            countSlotLessons[i] = countLessonsInSlots(subject, group, kind, day, sorts.slot(lessonSlots[i]));
        }

        return (RealExpr) ctx.mkAdd(countSlotLessons);
    }

    private RealExpr countLessonsInSlots(Expr subject, Expr group, Expr kind, Expr day, Expr slot) {
        LessonParity[] lessonParities = LessonParity.values();
        RealExpr[] countLessonParity = new RealExpr[lessonParities.length];

        for (int i = 0; i < lessonParities.length; i++) {
            countLessonParity[i] = countLessonsInSlots(subject, group, kind, day, slot, lessonParities[i]);
        }

        return (RealExpr) ctx.mkAdd(countLessonParity);
    }

    private RealExpr countLessonsInSlots(Expr subject, Expr group, Expr kind, Expr day, Expr slot, LessonParity parity) {
        checkExprsSort(sorts.subject(), subject);
        checkExprsSort(sorts.group(), group);
        checkExprsSort(sorts.kind(), kind);
        checkExprsSort(sorts.dayOfWeak(), day);
        checkExprsSort(sorts.slot(), slot);

        Expr slotItem = ctx.mkApp(func.schedule(), group, day, slot);

        switch (parity) {
            case always:
                return (RealExpr) ctx.mkITE(
                        ctx.mkAnd(
                                sorts.isSingleItemExpr(slotItem),
                                sorts.isNotBlankLessonExpr(sorts.singleItemLesson(slotItem)),
                                ctx.mkEq(sorts.lessonKind(sorts.singleItemLesson(slotItem)), kind),
                                ctx.mkEq(sorts.lessonSubject(sorts.singleItemLesson(slotItem)), subject)
                        ),
                        ctx.mkReal(1, 1),
                        ctx.mkReal(0)
                );
            case numerator:
                return (RealExpr) ctx.mkITE(
                        ctx.mkAnd(
                                sorts.hasNotEmptyNumerator(slotItem),
                                ctx.mkEq(sorts.lessonKind(sorts.pairItemNumerator(slotItem)), kind),
                                ctx.mkEq(sorts.lessonSubject(sorts.pairItemNumerator(slotItem)), subject)
                        ),
                        ctx.mkReal(1, 2),
                        ctx.mkReal(0)
                );
            case denominator:
                return (RealExpr) ctx.mkITE(
                        ctx.mkAnd(
                                sorts.hasNotEmptyDenominator(slotItem),
                                ctx.mkEq(sorts.lessonKind(sorts.pairItemDenominator(slotItem)), kind),
                                ctx.mkEq(sorts.lessonSubject(sorts.pairItemDenominator(slotItem)), subject)
                        ),
                        ctx.mkReal(1, 2),
                        ctx.mkReal(0)
                );
            default:
                return null;
        }
    }

    private BoolExpr validDayForTwoGroups(Expr group1, Expr group2, Expr day) {
        checkExprsSort(sorts.group(), group1, group2);
        checkExprsSort(sorts.dayOfWeak(), day);

        LessonSlot[] slots = LessonSlot.values();
        BoolExpr[] validSlots = new BoolExpr[slots.length];

        for (int i = 0; i < slots.length; i++) {
            validSlots[i] = validSlotItemsForTwoGroups(group1, group2, day, sorts.slot(slots[i]));
        }

        return ctx.mkAnd(validSlots);
    }

    private BoolExpr validSlotItemsForTwoGroups(Expr group1, Expr group2, Expr day, Expr slot) {
        checkExprsSort(sorts.group(), group1, group2);
        checkExprsSort(sorts.dayOfWeak(), day);
        checkExprsSort(sorts.slot(), slot);
        Expr slotItem1 = ctx.mkApp(func.schedule(), group1, day, slot);
        Expr slotItem2 = ctx.mkApp(func.schedule(), group2, day, slot);

        return ctx.mkOr(
                ctx.mkAnd(
                        sorts.isSingleItemExpr(slotItem1),
                        sorts.isSingleItemExpr(slotItem2),
                        validLessonsForSameSlot(sorts.singleItemLesson(slotItem1), sorts.singleItemLesson(slotItem2))
                ),
                ctx.mkAnd(
                        sorts.isPairItemExpr(slotItem1),
                        sorts.isPairItemExpr(slotItem2),
                        validLessonsForSameSlot(sorts.pairItemNumerator(slotItem1), sorts.pairItemNumerator(slotItem2)),
                        validLessonsForSameSlot(sorts.pairItemDenominator(slotItem1), sorts.pairItemDenominator(slotItem2))
                )
        );
    }

    private BoolExpr validSlotsInDay(Expr group, Expr day) throws IllegalArgumentException {
        checkExprsSort(sorts.group(), group);
        checkExprsSort(sorts.dayOfWeak(), day);

        Expr blankSlotItem = ctx.mkApp(sorts.singleSItemDecl(), ctx.mkApp(sorts.blankLessonDecl()));
        Expr blankPairSlotItem = ctx.mkApp(sorts.pairSItemDecl(), ctx.mkApp(sorts.blankLessonDecl()), ctx.mkApp(sorts.blankLessonDecl()));

        LessonSlot[] slots = LessonSlot.values();
        BoolExpr[] emptySlots = new BoolExpr[slots.length];
        BoolExpr[] nonEmptySlots = new BoolExpr[slots.length];

        for (int i = 0; i < emptySlots.length; i++) {
            emptySlots[i] = ctx.mkEq(ctx.mkApp(func.schedule(), group, day, sorts.slot(slots[i])), blankSlotItem);
            nonEmptySlots[i] = ctx.mkAnd(
                    ctx.mkNot(emptySlots[i]),
                    ctx.mkNot(
                            ctx.mkEq(ctx.mkApp(func.schedule(), group, day, sorts.slot(slots[i])), blankPairSlotItem)
                    )
            );
        }

        int noOfPatterns = slots.length *
                (MAX_LESSONS_PER_DAY - MIN_LESSONS_PER_DAY + (MAX_LESSONS_PER_DAY + MIN_LESSONS_PER_DAY) / 2);
        BoolExpr[] validPatterns = new BoolExpr[noOfPatterns + 1];
        int l = 0;
        for (int k = MIN_LESSONS_PER_DAY; k <= MAX_LESSONS_PER_DAY; k++) {
            for (int j = 0; j < slots.length - k; j++) {
                BoolExpr[] validSlots = new BoolExpr[slots.length];
                for (int i = j; i < k; i++) {
                    validSlots[i] = nonEmptySlots[j];
                }
                if (slots.length - k >= 0) {
                    System.arraycopy(emptySlots, k, validSlots, k, slots.length - k);
                }
                validPatterns[l++] = ctx.mkAnd(validSlots);
            }
        }

        BoolExpr[] validSlots = new BoolExpr[slots.length];
        System.arraycopy(emptySlots, 0, validSlots, 0, slots.length);

        validPatterns[l] = ctx.mkAnd(validSlots);
        return ctx.mkOr(validPatterns);
    }

    // lesson1 and lesson2 are in same slot item
    private BoolExpr validLessonsForSameSlot(Expr lesson1, Expr lesson2) {
        checkExprsSort(sorts.lesson(), lesson1, lesson2);

        return ctx.mkOr(
                ctx.mkAnd(sorts.isBlankLessonExpr(lesson1), sorts.isBlankLessonExpr(lesson2)),
                ctx.mkAnd(
                        sorts.isBlankLessonExpr(lesson1),
                        sorts.isNotBlankLessonExpr(lesson2),
                        ctx.mkEq(sorts.lessonKind(lesson2), sorts.kind(LessonKind.lec))
                ),
                ctx.mkAnd(
                        sorts.isNotBlankLessonExpr(lesson1),
                        sorts.isBlankLessonExpr(lesson2),
                        ctx.mkEq(sorts.lessonKind(lesson1), sorts.kind(LessonKind.lec))
                ),
                ctx.mkAnd(
                        sorts.isNotBlankLessonExpr(lesson1),
                        sorts.isNotBlankLessonExpr(lesson2),
                        ctx.mkEq(sorts.lessonSubject(lesson1), sorts.lessonSubject(lesson2)),
                        ctx.mkEq(sorts.lessonKind(lesson1), sorts.kind(LessonKind.lec)),
                        ctx.mkEq(sorts.lessonKind(lesson2), sorts.kind(LessonKind.lec)),
                        ctx.mkEq(sorts.lessonTutor(lesson1), sorts.lessonTutor(lesson2)),
                        ctx.mkEq(sorts.lessonRoom(lesson1), sorts.lessonRoom(lesson2))
                ),
                ctx.mkAnd(
                        sorts.isNotBlankLessonExpr(lesson1),
                        sorts.isNotBlankLessonExpr(lesson2),
                        ctx.mkNot(ctx.mkEq(sorts.lessonKind(lesson1), sorts.kind(LessonKind.lec))),
                        ctx.mkNot(ctx.mkEq(sorts.lessonKind(lesson2), sorts.kind(LessonKind.lec))),
                        ctx.mkNot(ctx.mkEq(sorts.lessonTutor(lesson1), sorts.lessonTutor(lesson2))),
                        ctx.mkNot(ctx.mkEq(sorts.lessonRoom(lesson1), sorts.lessonRoom(lesson2)))
                )
        );
    }

    private static void checkExprsSort(Sort sort, Expr... exprs) throws IllegalArgumentException {
        for (Expr expr : exprs) {
            if (!expr.getSort().equals(sort)) {
                String msg = String.format(
                        "Invalid sort of expression: %s. Expected: %s, actual: %s",
                        expr,
                        sort.getName(),
                        expr.getSort().getName()
                );
                throw new IllegalArgumentException(msg);
            }
        }
    }

}
