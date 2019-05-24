package ru.bmstu.schedule.smtgen.model;

import com.microsoft.z3.*;

public class ScheduleFunctions {

    private ScheduleSorts sorts;
    private Context ctx;

    public ScheduleFunctions(ScheduleSorts sorts) {
        this.sorts = sorts;
        this.ctx = this.sorts.getContext();
    }

    // schedule: (group, dayOfWeak, slot) -> slot-item
    public FuncDecl schedule() {
        return ctx.mkFuncDecl(
                "schedule",
                new Sort[]{sorts.group(), sorts.dayOfWeak(), sorts.slot()},
                sorts.slotItem()
        );
    }

}
