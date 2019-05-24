package ru.bmstu.schedule.smtgen.model;

import com.microsoft.z3.*;
import ru.bmstu.schedule.smtgen.DayOfWeak;
import ru.bmstu.schedule.smtgen.LessonKind;

import java.util.Arrays;

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
    private EnumSort parity;
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
        initConstructors();
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

    EnumSort kind() {
        return kind;
    }

    Expr kind(Enum<LessonKind> kind) {
        return kind().getConst(kind.ordinal());
    }

    EnumSort slot() {
        return slot;
    }

    Expr slot(Enum<LessonSlots> slot) {
        return slot().getConst(slot.ordinal());
    }

    DatatypeSort subject() {
        return subject;
    }

    EnumSort parity() {
        return parity;
    }

    Expr parity(Enum<LessonParity> parity) {
        return parity().getConst(parity.ordinal());
    }

    DatatypeSort tutor() {
        return tutor;
    }

    DatatypeSort room() {
        return room;
    }

    DatatypeSort group() {
        return group;
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

    FuncDecl lessonDecl() {
        return lessonConstructor.ConstructorDecl();
    }

    Expr lessonSubject(Expr lesson) {
        return ctx.mkApp(lessonConstructor.getAccessorDecls()[0], lesson);
    }

    Expr lessonKind(Expr lesson) {
        return ctx.mkApp(lessonConstructor.getAccessorDecls()[1], lesson);
    }

    Expr lessonTutor(Expr lesson) {
        return ctx.mkApp(lessonConstructor.getAccessorDecls()[2], lesson);
    }

    Expr lessonRoom(Expr lesson) {
        return ctx.mkApp(lessonConstructor.getAccessorDecls()[3], lesson);
    }

    FuncDecl blankLessonDecl() {
        return blankLessonConstructor.ConstructorDecl();
    }

    BoolExpr isBlankLessonExpr(Expr lessonExpr) {
        return ctx.mkAnd(
                ctx.mkBool(lessonExpr.getSort().equals(lesson)),
                (BoolExpr) ctx.mkApp(blankLessonConstructor.getTesterDecl(), lessonExpr)
        );
    }

    BoolExpr isNotBlankLessonExpr(Expr lessonExpr) {
        return ctx.mkAnd(
                ctx.mkBool(lessonExpr.getSort().equals(lesson)),
                (BoolExpr) ctx.mkApp(lessonConstructor.getTesterDecl(), lessonExpr)
        );
    }

    BoolExpr isSingleItemExpr(Expr slotItem) {
        return ctx.mkAnd(
                ctx.mkBool(slotItem.getSort().equals(lesson)),
                (BoolExpr) ctx.mkApp(singleSItemConstructor.getTesterDecl(), slotItem)
        );
    }

    BoolExpr isPairItemExpr(Expr slotItem) {
        return ctx.mkAnd(
                ctx.mkBool(slotItem.getSort().equals(slot)),
                (BoolExpr) ctx.mkApp(pairSItemConstructor.getTesterDecl(), slotItem)
        );
    }

    Expr singleItemLesson(Expr singleSlotItem) {
        return singleSItemConstructor.getAccessorDecls()[0].apply(singleSlotItem);
    }

    Expr pairItemNumerator(Expr pairSlotItem) {
        return pairSItemConstructor.getAccessorDecls()[0].apply(pairSlotItem);
    }

    Expr pairItemDenominator(Expr pairSlotItem) {
        return pairSItemConstructor.getAccessorDecls()[1].apply(pairSlotItem);
    }

    BoolExpr hasNotEmptyNumerator(Expr slotItem) {
        return ctx.mkAnd(
                ctx.mkBool(slotItem.getSort().equals(slotItem())),
                isPairItemExpr(slotItem),
                (BoolExpr) ctx.mkApp(lessonConstructor.getTesterDecl(), pairItemNumerator(slotItem))
        );
    }

    BoolExpr hasNotEmptyDenominator(Expr slotItem) {
        return ctx.mkAnd(
                ctx.mkBool(slotItem.getSort().equals(slotItem())),
                isPairItemExpr(slotItem),
                (BoolExpr) ctx.mkApp(lessonConstructor.getTesterDecl(), pairItemDenominator(slotItem))
        );
    }

    private void initSorts() {
        dayOfWeak = mkCustomEnumSort(ctx, DayOfWeak.class);
        kind = mkCustomEnumSort(ctx, LessonKind.class);
        slot = mkCustomEnumSort(ctx, LessonSlots.class);
        parity = mkCustomEnumSort(ctx, LessonParity.class);
        subject = ctx.mkDatatypeSort("Subject", new Constructor[]{subjectConstructor});
        tutor = ctx.mkDatatypeSort("Tutor", new Constructor[]{tutorConstructor});
        room = ctx.mkDatatypeSort("Room", new Constructor[]{roomConstructor});
        group = ctx.mkDatatypeSort("Group", new Constructor[]{groupConstructor});
        lesson = ctx.mkDatatypeSort("Lesson", new Constructor[]{blankLessonConstructor, lessonConstructor});
        slotItem = ctx.mkDatatypeSort("SlotItem", new Constructor[]{singleSItemConstructor, pairSItemConstructor});
    }

    private void initConstructors() {
        subjectConstructor = ctx.mkConstructor(
                "mk-subject",
                "is-subject",
                new String[]{"id-of"},
                new Sort[]{ctx.mkIntSort()},
                new int[]{0}
        );
        tutorConstructor = ctx.mkConstructor(
                "mk-tutor",
                "is-tutor",
                new String[]{"id-of"},
                new Sort[]{ctx.mkIntSort()},
                new int[]{0}
        );
        roomConstructor = ctx.mkConstructor(
                "mk-room",
                "is-room",
                new String[]{"id-of"},
                new Sort[]{ctx.mkIntSort()},
                new int[]{0}
        );
        groupConstructor = ctx.mkConstructor(
                "mk-group",
                "is-group",
                new String[]{"id-of"},
                new Sort[]{ctx.mkIntSort()},
                new int[]{0}
        );
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
    }

    private static EnumSort mkCustomEnumSort(Context ctx, Class<? extends Enum<?>> enumType) {
        String[] symbols = (String[]) Arrays.stream(enumType.getEnumConstants())
                .map(Enum::name)
                .toArray();

        return ctx.mkEnumSort(enumType.getSimpleName(), symbols);
    }

}