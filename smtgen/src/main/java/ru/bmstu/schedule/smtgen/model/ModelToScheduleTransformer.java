package ru.bmstu.schedule.smtgen.model;

import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Model;
import ru.bmstu.schedule.entity.*;
import ru.bmstu.schedule.smtgen.ScheduleDay;
import ru.bmstu.schedule.smtgen.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static ru.bmstu.schedule.smtgen.Z3Utils.checkExprsSort;
import static ru.bmstu.schedule.smtgen.Z3Utils.toBoolean;

public class ModelToScheduleTransformer {

    private SmtScheduleModelGenerator modelGenerator;

    private Context ctx;
    private ScheduleFunctions funcs;
    private ScheduleSorts sorts;

    private Map<Integer, Subject> idToSubj;
    private Map<Integer, Lecturer> idToLecturer;
    private Map<Integer, StudyGroup> idToStudyGroup;
    private Map<Integer, Classroom> idToClassroom;
    private Map<LessonKind, ClassType> idToClassType;

    public ModelToScheduleTransformer(
            SmtScheduleModelGenerator modelGenerator,
            Map<Integer, Subject> idToSubj,
            Map<Integer, Lecturer> idToLecturer,
            Map<Integer, StudyGroup> idToStudyGroup,
            Map<Integer, Classroom> idToClassroom,
            Map<LessonKind, ClassType> kindToClassType
    ) {
        this.modelGenerator = modelGenerator;
        this.ctx = modelGenerator.getContext();
        this.funcs = modelGenerator.getFunctions();
        this.sorts = modelGenerator.getSorts();

        this.idToSubj = idToSubj;
        this.idToLecturer = idToLecturer;
        this.idToStudyGroup = idToStudyGroup;
        this.idToClassroom = idToClassroom;
        this.idToClassType = kindToClassType;
    }

    public Map<StudyGroup, Schedule> transform() throws RuntimeException {
        if (!modelGenerator.satisfies()) {
            throw new RuntimeException("Unable to transform model to schedule, because model is not satisfiable.");
        }

        Optional<Model> modelOpt = modelGenerator.getSmtModel();
        if (!modelOpt.isPresent()) {
            throw new RuntimeException("No model provided");
        }

        Model model = modelOpt.get();

        Map<StudyGroup, Schedule> schedulesOfGroups = new HashMap<>();

        Expr[] groupsExpr = modelGenerator.getGroupsConstants();
        Expr[] daysExpr = modelGenerator.getDaysConstants();
        Expr[] slotsExpr = modelGenerator.getSlotsConstants();

        for (Expr groupExpr : groupsExpr) {
            Integer groupId = Integer.valueOf(sorts.groupId(groupExpr).toString());
            StudyGroup studyGroup = idToStudyGroup.get(groupId);
            Schedule schedule = new Schedule();
            int dayNo = 0;

            for (Expr dayExpr : daysExpr) {
                ScheduleDay scheduleDay = new ScheduleDay();
                scheduleDay.setDayOfWeak(sorts.dayOfWeekEnum(dayExpr));

                LessonItem[] items = new LessonItem[7];
                int itemNo = 0;

                for (Expr slotExpr : slotsExpr) {
                    // Get slot item interpretation from current model
                    Expr slotItemInterpExpr = model.eval(ctx.mkApp(funcs.schedule(), groupExpr, dayExpr, slotExpr), false);
                    Expr[] itemLessonsExprs;
                    if (toBoolean(sorts.isSingleItemExpr(slotItemInterpExpr))) {
                        itemLessonsExprs = new Expr[]{
                                sorts.singleItemLesson(slotItemInterpExpr)
                        };
                    } else {
                        boolean notEmptyNumerator = toBoolean(sorts.hasNotEmptyNumerator(slotItemInterpExpr));
                        boolean notEmptyDenominator = toBoolean(sorts.hasNotEmptyDenominator(slotItemInterpExpr));
                        itemLessonsExprs = new Expr[2];

                        if (notEmptyNumerator && notEmptyDenominator) {
                            itemLessonsExprs[0] = sorts.pairItemNumerator(slotItemInterpExpr);
                            itemLessonsExprs[1] = sorts.pairItemDenominator(slotItemInterpExpr);
                        } else if (notEmptyNumerator) {
                            itemLessonsExprs[0] = sorts.pairItemNumerator(slotItemInterpExpr);
                        } else if (notEmptyDenominator) {
                            itemLessonsExprs[1] = sorts.pairItemDenominator(slotItemInterpExpr);
                        } else {
                            continue;
                        }
                    }

                    if (itemLessonsExprs.length == 2) {
                        items[itemNo] = new PairLessonItem(
                                itemNo,
                                lessonExprToLesson(itemLessonsExprs[0]),
                                lessonExprToLesson(itemLessonsExprs[1])
                        );

                    } else {
                        items[itemNo] = new SingleLessonItem(itemNo, lessonExprToLesson(itemLessonsExprs[0]));
                    }
                    itemNo++;
                }
                scheduleDay.setItems(items);
                schedule.setDay(dayNo++, scheduleDay);
            }

            schedulesOfGroups.put(studyGroup, schedule);
        }

        return schedulesOfGroups;
    }

    private Lesson lessonExprToLesson(Expr lessonExpr) {
        checkExprsSort(sorts.lesson(), lessonExpr);

        if (toBoolean(sorts.isBlankLessonExpr(lessonExpr))) {
            return null;
        }
        Lesson lesson = new Lesson();

        Integer subjId = Integer.valueOf(sorts.subjectId(sorts.lessonSubject(lessonExpr)).getString());
        Integer tutorId = Integer.valueOf(sorts.tutorId(sorts.lessonTutor(lessonExpr)).getString());
        Integer roomId = Integer.valueOf(sorts.roomId(sorts.lessonRoom(lessonExpr)).getString());
        LessonKind kind = sorts.kindEnum(sorts.lessonKind(lessonExpr));

        lesson.setSubject(idToSubj.get(subjId));
        lesson.setLecturer(idToLecturer.get(tutorId));
        lesson.setClassroom(idToClassroom.get(roomId));
        lesson.setClassType(idToClassType.get(kind));

        return lesson;
    }

}
