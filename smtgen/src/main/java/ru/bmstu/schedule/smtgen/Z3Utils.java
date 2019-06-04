package ru.bmstu.schedule.smtgen;

import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;

public final class Z3Utils {

    private Z3Utils() {
    }

    public static void checkExprsSort(Sort sort, Expr... exprs) throws IllegalArgumentException {
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
