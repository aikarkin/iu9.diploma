import com.microsoft.z3.*;

public class JavaExample2 {

    public static void main(String[] args) {
        Context ctx = new Context();

        Solver solver = ctx.mkSolver();

        EnumSort dayOfWeak = ctx.mkEnumSort("DayOfWeak", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat");
        EnumSort lessonSlots = ctx.mkEnumSort("Lesson", "A", "B", "C", "D", "E", "F", "G");
        EnumSort lKindSort = ctx.mkEnumSort("LessonType", "Lec", "Sem", "Lab");

        DatatypeSort subjectSort = ctx.mkDatatypeSort(
                "Subject",
                new Constructor[]{
                        ctx.mkConstructor(
                                "mk-subject",
                                "is-subject",
                                new String[]{"name-of"},
                                new Sort[]{ctx.mkStringSort()},
                                new int[]{0}
                        )
                });

        DatatypeSort tutorSort = ctx.mkDatatypeSort(
                "Tutor",
                new Constructor[]{
                        ctx.mkConstructor(
                                "mk-tutor",
                                "is-tutor",
                                new String[]{"name-of"},
                                new Sort[]{ctx.mkStringSort()},
                                new int[]{0}
                        )
                });

        DatatypeSort roomSort = ctx.mkDatatypeSort(
                "Room",
                new Constructor[]{
                        ctx.mkConstructor(
                                "mk-room",
                                "is-room",
                                new String[]{"label-of"},
                                new Sort[]{ctx.mkStringSort()},
                                new int[]{0}
                        )
                });

        DatatypeSort groupSort = ctx.mkDatatypeSort(
                "Group",
                new Constructor[]{
                        ctx.mkConstructor(
                                "mk-group",
                                "is-group",
                                new String[]{"cipher-of"},
                                new Sort[]{ctx.mkStringSort()},
                                new int[]{0}
                        )
                });

        DatatypeSort lessonSort = ctx.mkDatatypeSort(
                "Lesson",
                new Constructor[]{
                        ctx.mkConstructor(
                                "mk-lesson",
                                "is-not-blank",
                                new String[]{"subject", "kind", "tutor", "room"},
                                new Sort[]{subjectSort, lKindSort, tutorSort, roomSort},
                                new int[]{0, 1, 2, 3}
                        ),
                        ctx.mkConstructor(
                                "mk-blank-lesson",
                                "is-blank",
                                null,
                                null,
                                null
                        )
                }
        );


//        Expr mathConst = subjectSort.getConstructors()[0].apply(ctx.mkString("Math"));
//        System.out.println(mathConst.getSExpr());
//        System.out.println(mathConst.getSort());
//        System.out.println(mathConst.getClass());

    }

}
