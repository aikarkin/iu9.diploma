package ru.bmstu.schedule.smtgen.model;

import com.microsoft.z3.*;
import ru.bmstu.schedule.smtgen.DayOfWeak;
import ru.bmstu.schedule.smtgen.LessonKind;
import ru.bmstu.schedule.smtgen.SubjectsPerWeek;

import java.util.*;

import static ru.bmstu.schedule.smtgen.Z3Utils.checkExprsSort;

public class SmtScheduleModelGenerator {

    private Map<Integer, SubjectsPerWeek> totalSubjectsPerWeak;
    private Solver solver;
    private Status modelStatus;
    private Context ctx;
    private ScheduleSorts sorts;
    private ScheduleFunctions func;
    private ScheduleAsserts asserts;

    private List<TutorForLesson> tutorForLessons;
    private List<Integer> rooms;
    private List<Integer> groups;

    private Map<Integer, Expr> tutorConstById;
    private Map<Integer, Expr> subjConstById;

    private List<Expr> roomsConsts;
    private List<Expr> groupsConsts;

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

        createSubjectsConstants();
        createRoomsConsts();
        createGroupsConstants();
        createTutorsConstants();
    }

    public Expr[] getGroupsConstants() {
        return groupsConsts.toArray(new Expr[0]);
    }

    public Expr[] getSlotsConstants() {
        return sorts.slot().getConsts();
    }

    public Expr[] getDaysConstants() {
        return sorts.dayOfWeak().getConsts();
    }

    public Context getContext() {
        return ctx;
    }

    public ScheduleSorts getSorts() {
        return sorts;
    }

    public ScheduleFunctions getFunctions() {
        return func;
    }

    public Status check() {
        if (modelStatus == null) {
            if (solver == null) {
                solver = ctx.mkSolver();
                solver.add(validSchedule());
            }
            modelStatus = solver.check();
        }

        return modelStatus;
    }

    public boolean satisfies() {
        return check() == Status.SATISFIABLE;
    }

    public Optional<Model> getSmtModel() {
        return Optional.ofNullable(satisfies() ? solver.getModel() : null);
    }

    private void createTutorsConstants() {
        this.tutorConstById = new HashMap<>();

        for (TutorForLesson lesson : tutorForLessons) {
            int tutorId = lesson.getTutorId();
            tutorConstById.put(tutorId, ctx.mkApp(sorts.tutorDecl(), ctx.mkInt(tutorId)));
        }
    }

    private RealExpr subjCountToReal(double count) {
        double trunc = Math.floor(count);

        if (trunc == count) {
            return ctx.mkReal((int) count);
        } else {
            return (RealExpr) ctx.mkAdd(ctx.mkReal((int) trunc), ctx.mkReal(1, 2));
        }
    }

    private void createRoomsConsts() {
        this.roomsConsts = new ArrayList<>();

        for (int rId : rooms) {
            this.roomsConsts.add(ctx.mkApp(sorts.roomDecl(), ctx.mkInt(rId)));
        }
    }

    private void createSubjectsConstants() {
        subjConstById = new HashMap<>();
        for (int subjId : totalSubjectsPerWeak.keySet()) {
            this.subjConstById.put(subjId, ctx.mkApp(sorts.subjectDecl(), ctx.mkInt(subjId)));
        }
    }

    private void createGroupsConstants() {
        this.groupsConsts = new ArrayList<>();

        for (int groupId : groups) {
            groupsConsts.add(ctx.mkApp(sorts.groupDecl(), ctx.mkInt(groupId)));
        }
    }

    private BoolExpr exprIsOneOf(Expr expr, Collection<Expr> constants) {
        BoolExpr[] validExpr = new BoolExpr[constants.size()];
        int i = 0;

        for (Expr cnst : constants) {
            validExpr[i++] = ctx.mkEq(expr, cnst);
        }

        return ctx.mkOr(validExpr);
    }

    private BoolExpr validSubject(Expr subject) {
        checkExprsSort(sorts.subject(), subject);
        return exprIsOneOf(subject, subjConstById.values());
    }

    private BoolExpr validRoom(Expr room) {
        checkExprsSort(sorts.room(), room);
        return exprIsOneOf(room, roomsConsts);
    }

    private BoolExpr validTutorForSubject(Expr tutor, Expr subj, Expr kind) {
        int i = 0;
        BoolExpr[] validTutorForSubj = new BoolExpr[tutorForLessons.size()];

        for (TutorForLesson tutorForLesson : tutorForLessons) {
            validTutorForSubj[i++] = ctx.mkAnd(
                    ctx.mkEq(subj, subjConstById.get(tutorForLesson.getSubjectId())),
                    ctx.mkEq(tutor, tutorConstById.get(tutorForLesson.getTutorId())),
                    ctx.mkEq(kind, sorts.kind(tutorForLesson.getKind()))
            );
        }

        return ctx.mkOr(validTutorForSubj);
    }

    private BoolExpr validNotEmptyLesson(Expr lesson) {
        return ctx.mkAnd(
                validSubject(sorts.lessonSubject(lesson)),
                validRoom(sorts.lessonRoom(lesson)),
                validTutorForSubject(sorts.lessonTutor(lesson), sorts.lessonSubject(lesson), sorts.lessonKind(lesson))
        );
    }

    private BoolExpr validLesson(Expr lesson) {
        return ctx.mkImplies(sorts.isNotBlankLessonExpr(lesson), validNotEmptyLesson(lesson));
    }

    private BoolExpr validSlotForGroupAndDay(Expr group, Expr day, Expr slot) {
        Expr slotItem = ctx.mkApp(func.schedule(), group, day, slot);
        return ctx.mkOr(
                ctx.mkAnd(
                        sorts.isSingleItemExpr(slotItem),
                        validLesson(sorts.singleItemLesson(slotItem))
                ),
                ctx.mkAnd(
                        sorts.isPairItemExpr(slotItem),
                        validLesson(sorts.pairItemNumerator(slotItem)),
                        validLesson(sorts.pairItemDenominator(slotItem))
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
        int n = subjConstById.size(), i = 0;
        BoolExpr[] validTotalSubj = new BoolExpr[n];

        for (int subjId : totalSubjectsPerWeak.keySet()) {
            RealExpr lecCount, semCount, labCount;
            Expr subjExpr = subjConstById.get(subjId);
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
        BoolExpr[] validForGroup = new BoolExpr[totalGroups];

        for (int i = 0; i < totalGroups; i++) {
            validForGroup[i] = validScheduleForGroup(groupsConsts.get(i));
            for (int j = i + 1; j < totalGroups; j++) {
                validForTwoGroups[k++] = asserts.validWeeksForTwoGroups(groupsConsts.get(i), groupsConsts.get(j));
            }
        }

        return ctx.mkAnd(
                ctx.mkAnd(validForGroup),
                ctx.mkAnd(validForTwoGroups)
        );
    }

}
