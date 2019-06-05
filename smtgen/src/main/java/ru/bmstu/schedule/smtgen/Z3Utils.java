package ru.bmstu.schedule.smtgen;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;
import com.microsoft.z3.enumerations.Z3_lbool;

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

    public static boolean toBoolean(BoolExpr expr) {
        return expr.getBoolValue() == Z3_lbool.Z3_L_TRUE;
    }

}
