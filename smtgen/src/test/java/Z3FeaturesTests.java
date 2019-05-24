import com.microsoft.z3.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Z3FeaturesTests {
    private static Context ctx;

    @BeforeAll
    static void setUp() {
        ctx = new Context();
    }


    @Test
    public void testSets() {
        ArrayExpr intSet = ctx.mkEmptySet(ctx.mkIntSort());

        IntNum setMember = ctx.mkInt(12);
        intSet = ctx.mkSetAdd(intSet, setMember);

        Solver solver = ctx.mkSolver();

        solver.add(ctx.mkEq(ctx.mkSetMembership(ctx.mkInt(12), intSet), ctx.mkBool(true)));
        assertEquals(solver.check(), Status.SATISFIABLE);
        solver.add(ctx.mkEq(ctx.mkSetMembership(ctx.mkInt(10), intSet), ctx.mkBool(true)));
        assertEquals(solver.check(), Status.UNSATISFIABLE);
    }

}
