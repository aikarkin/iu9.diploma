package ru.bmstu.schedule.smtgen.model;

import com.microsoft.z3.*;
import ru.bmstu.schedule.smtgen.DayOfWeak;
import ru.bmstu.schedule.smtgen.LessonKind;

import java.util.Arrays;

import static ru.bmstu.schedule.smtgen.Z3Utils.checkExprsSort;

public class ScheduleSorts {

    private Context ctx;

    private Constructor subjectConstructor;
    private Constructor tutorConstructor;
    private Constructor roomConstructor;
    private Constructor groupConstructor;
    private Constructor lessonConstructor;
    private Constructor blankLessonConstructor;
    private Constructor singleSItemConstructor;
    private Constructor pairSItemConstructor;

    private EnumSort dayOfWeak;
    private EnumSort kind;
    private EnumSort slot;
    private DatatypeSort subject;
    private DatatypeSort tutor;
    private DatatypeSort room;
    private DatatypeSort group;
    private DatatypeSort lesson;
    private DatatypeSort slotItem;

    public ScheduleSorts(Context ctx) {
        this.ctx = ctx;
        initSorts();
    }

    Context getContext() {
        return ctx;
    }

    EnumSort dayOfWeak() {
        return dayOfWeak;
    }

    Expr dayOfWeak(Enum<DayOfWeak> day) {
        return dayOfWeak().getConst(day.ordinal());
    }

    DayOfWeak dayOfWeekEnum(Expr day) {
        checkExprsSort(dayOfWeak(), day);
        String strVal = day.getString();
        return DayOfWeak.valueOf(strVal);
    }

    LessonKind kindEnum(Expr kind) {
        checkExprsSort(kind(), kind);
        String strVal = kind.getString();
        return LessonKind.valueOf(strVal);
    }

    EnumSort kind() {
        return kind;
    }

    Expr kind(Enum<LessonKind> kind) {
        return kind().getConst(kind.ordinal());
    }

    EnumSort slot() {
        return slot;
    }

    Expr slot(Enum<LessonSlot> slot) {
        return slot().getConst(slot.ordinal());
    }

    DatatypeSort subject() {
        return subject;
    }

    IntExpr subjectId(Expr subj) {
        checkExprsSort(subject(), subj);
        return (IntExpr) ctx.mkApp(subjectConstructor.getAccessorDecls()[0], subj);
    }

    DatatypeSort tutor() {
        return tutor;
    }

    IntExpr tutorId(Expr tutor) {
        checkExprsSort(tutor(), tutor);
        return (IntExpr) ctx.mkApp(tutorConstructor.getAccessorDecls()[0], tutor);
    }

    DatatypeSort room() {
        return room;
    }

    IntExpr roomId(Expr room) {
        checkExprsSort(room(), room);
        return (IntExpr) ctx.mkApp(roomConstructor.getAccessorDecls()[0], room);
    }

    DatatypeSort group() {
        return group;
    }

    IntExpr groupId(Expr group) {
        checkExprsSort(group(), group);
        return (IntExpr) ctx.mkApp(groupConstructor.getAccessorDecls()[0], group);
    }

    DatatypeSort lesson() {
        return lesson;
    }

    DatatypeSort slotItem() {
        return slotItem;
    }

    FuncDecl subjectDecl() {
        return subjectConstructor.ConstructorDecl();
    }

    FuncDecl tutorDecl() {
        return tutorConstructor.ConstructorDecl();
    }

    FuncDecl roomDecl() {
        return roomConstructor.ConstructorDecl();
    }

    FuncDecl groupDecl() {
        return groupConstructor.ConstructorDecl();
    }

    FuncDecl singleSItemDecl() {
        return singleSItemConstructor.ConstructorDecl();
    }

    FuncDecl pairSItemDecl() {
        return pairSItemConstructor.ConstructorDecl();
    }

    FuncDecl blankLessonDecl() {
        return blankLessonConstructor.ConstructorDecl();
    }


    Expr lessonSubject(Expr lesson) {
        checkExprsSort(lesson(), lesson);
        return ctx.mkApp(lessonConstructor.getAccessorDecls()[0], lesson);
    }

    Expr lessonKind(Expr lesson) {
        checkExprsSort(lesson(), lesson);
        return ctx.mkApp(lessonConstructor.getAccessorDecls()[1], lesson);
    }

    Expr lessonTutor(Expr lesson) {
        checkExprsSort(lesson(), lesson);
        return ctx.mkApp(lessonConstructor.getAccessorDecls()[2], lesson);
    }

    Expr lessonRoom(Expr lesson) {
        checkExprsSort(lesson(), lesson);
        return ctx.mkApp(lessonConstructor.getAccessorDecls()[3], lesson);
    }

    BoolExpr isBlankLessonExpr(Expr lessonExpr) {
        checkExprsSort(lesson(), lessonExpr);
        return (BoolExpr) ctx.mkApp(blankLessonConstructor.getTesterDecl(), lessonExpr);
    }

    BoolExpr isNotBlankLessonExpr(Expr lessonExpr) {
        checkExprsSort(lesson(), lessonExpr);
        return (BoolExpr) ctx.mkApp(lessonConstructor.getTesterDecl(), lessonExpr);
    }

    BoolExpr isSingleItemExpr(Expr slotItem) {
        checkExprsSort(slotItem(), slotItem);
        return (BoolExpr) ctx.mkApp(singleSItemConstructor.getTesterDecl(), slotItem);
    }

    BoolExpr isPairItemExpr(Expr slotItem) {
        checkExprsSort(slotItem(), slotItem);
        return (BoolExpr) ctx.mkApp(pairSItemConstructor.getTesterDecl(), slotItem);
    }

    Expr singleItemLesson(Expr singleSlotItem) {
        checkExprsSort(slotItem(), singleSlotItem);
        return singleSItemConstructor.getAccessorDecls()[0].apply(singleSlotItem);
    }

    Expr pairItemNumerator(Expr pairSlotItem) {
        checkExprsSort(slotItem(), pairSlotItem);
        return pairSItemConstructor.getAccessorDecls()[0].apply(pairSlotItem);
    }

    Expr pairItemDenominator(Expr pairSlotItem) {
        checkExprsSort(slotItem(), pairSlotItem);
        return pairSItemConstructor.getAccessorDecls()[1].apply(pairSlotItem);
    }

    BoolExpr hasNotEmptyNumerator(Expr slotItem) {
        checkExprsSort(slotItem(), slotItem);
        return ctx.mkAnd(
                isPairItemExpr(slotItem),
                (BoolExpr) ctx.mkApp(lessonConstructor.getTesterDecl(), pairItemNumerator(slotItem))
        );
    }

    BoolExpr hasNotEmptyDenominator(Expr slotItem) {
        checkExprsSort(slotItem(), slotItem);
        return ctx.mkAnd(
                isPairItemExpr(slotItem),
                (BoolExpr) ctx.mkApp(lessonConstructor.getTesterDecl(), pairItemDenominator(slotItem))
        );
    }

    private void initSorts() {
        dayOfWeak = mkCustomEnumSort(ctx, DayOfWeak.class);
        kind = mkCustomEnumSort(ctx, LessonKind.class);
        slot = mkCustomEnumSort(ctx, LessonSlot.class);

        // Subject sort:
        subjectConstructor = ctx.mkConstructor(
                "mk-subject",
                "is-subject",
                new String[]{"id-of"},
                new Sort[]{ctx.mkIntSort()},
                new int[]{0}
        );
        subject = ctx.mkDatatypeSort("Subject", new Constructor[]{subjectConstructor});

        // Tutor sort:
        tutorConstructor = ctx.mkConstructor(
                "mk-tutor",
                "is-tutor",
                new String[]{"id-of"},
                new Sort[]{ctx.mkIntSort()},
                new int[]{0}
        );
        tutor = ctx.mkDatatypeSort("Tutor", new Constructor[]{tutorConstructor});

        // Room sort:
        roomConstructor = ctx.mkConstructor(
                "mk-room",
                "is-room",
                new String[]{"id-of"},
                new Sort[]{ctx.mkIntSort()},
                new int[]{0}
        );
        room = ctx.mkDatatypeSort("Room", new Constructor[]{roomConstructor});

        // Group sort:
        groupConstructor = ctx.mkConstructor(
                "mk-group",
                "is-group",
                new String[]{"id-of"},
                new Sort[]{ctx.mkIntSort()},
                new int[]{0}
        );
        group = ctx.mkDatatypeSort("Group", new Constructor[]{groupConstructor});

        // Lesson sort:
        lessonConstructor = ctx.mkConstructor(
                "mk-lesson",
                "is-lesson",
                new String[]{"subject-of", "kind-of", "tutor-of", "room-of"},
                new Sort[]{subject(), kind(), tutor(), room()},
                new int[]{0, 1, 2, 3}
        );
        blankLessonConstructor = ctx.mkConstructor(
                "mk-blank-lesson",
                "is-blank",
                null,
                null,
                null
        );
        lesson = ctx.mkDatatypeSort("Lesson", new Constructor[]{blankLessonConstructor, lessonConstructor});

        singleSItemConstructor = ctx.mkConstructor(
                "mk-item",
                "is-single",
                new String[]{"lesson-of"},
                new Sort[]{lesson()},
                new int[]{0}
        );
        pairSItemConstructor = ctx.mkConstructor(
                "mk-pair-item",
                "is-pair",
                new String[]{"numerator", "denominator"},
                new Sort[]{lesson(), lesson()},
                new int[]{0, 1}
        );
        slotItem = ctx.mkDatatypeSort("SlotItem", new Constructor[]{singleSItemConstructor, pairSItemConstructor});
    }

    private static EnumSort mkCustomEnumSort(Context ctx, Class<? extends Enum<?>> enumType) {
        String[] symbols = Arrays.stream(enumType.getEnumConstants())
                .map(Enum::name)
                .toArray(String[]::new);

        return ctx.mkEnumSort(enumType.getSimpleName(), symbols);
    }

}