
import org.junit.Test;
import ru.bmstu.schedule.parser.GroupDeserializer;
import ru.bmstu.schedule.parser.node.GroupNode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class GroupNodeParseTest {

    @Test
    public void testGroupWithDefaultDegree() {
        GroupNode groupNode = GroupDeserializer.parseGroupFromCipher("ИУ2-121");

        assertNotNull(groupNode);

        assertEquals(groupNode.getDegree(), GroupNode.Degree.BACHELOR);
        assertEquals(groupNode.getFacultyCipher() , "ИУ");
        assertEquals(groupNode.getDepartmentNumber(),2);
        assertEquals(groupNode.getTermNumber(), 12);
        assertEquals(groupNode.getGroupNumber(), 1);
    }

    @Test
    public void testGroupWithGradDegree() {
        GroupNode groupNode = GroupDeserializer.parseGroupFromCipher("ИУ2-12А");

        assertNotNull(groupNode);

        assertEquals(groupNode.getDegree(), GroupNode.Degree.PHILOSOPHY);
        assertEquals(groupNode.getFacultyCipher() , "ИУ");
        assertEquals(groupNode.getDepartmentNumber(),2);
        assertEquals(groupNode.getGroupNumber(), 2);
        assertEquals(groupNode.getTermNumber(), 1);
    }

    @Test
    public void testGroupWithMasterDegree() {
        GroupNode groupNode = GroupDeserializer.parseGroupFromCipher("ИУ4-32М (М) фвафыва");

        assertNotNull(groupNode);

        assertEquals(groupNode.getDegree(), GroupNode.Degree.MASTER); // английсская "A"
        assertEquals(groupNode.getFacultyCipher() , "ИУ");
        assertEquals(groupNode.getDepartmentNumber(),4);
        assertEquals(groupNode.getTermNumber(), 3);
        assertEquals(groupNode.getGroupNumber(), 2);
    }

    @Test
    public void testFacultyWithSingleDepartment() {
        GroupNode group = GroupDeserializer.parseGroupFromCipher("ЮР-31М (М)");
        assertNotNull(group);

        assertEquals(GroupNode.Degree.MASTER, group.getDegree());
        assertEquals("ЮР", group.getFacultyCipher());
        assertEquals(0, group.getDepartmentNumber());
        assertEquals(3, group.getTermNumber());
        assertEquals(1, group.getGroupNumber());

        group = GroupDeserializer.parseGroupFromCipher("ЮР-73");
        assertNotNull(group);

        assertEquals(GroupNode.Degree.BACHELOR, group.getDegree());
        assertEquals("ЮР", group.getFacultyCipher());
        assertEquals(0, group.getDepartmentNumber());
        assertEquals(7, group.getTermNumber());
        assertEquals(3, group.getGroupNumber());
    }

    @Test
    public void testDirtyGroupCipher() {
        GroupNode groupNode = GroupDeserializer.parseGroupFromCipher("ИУ4-");
        assertNull(groupNode);

        groupNode = GroupDeserializer.parseGroupFromCipher("-45");
        assertNull(groupNode);

        groupNode = GroupDeserializer.parseGroupFromCipher("-");
        assertNull(groupNode);

        groupNode = GroupDeserializer.parseGroupFromCipher("");
        assertNull(groupNode);

        groupNode = GroupDeserializer.parseGroupFromCipher("a");
        assertNull(groupNode);

        groupNode = GroupDeserializer.parseGroupFromCipher("4");
        assertNull(groupNode);

        groupNode = GroupDeserializer.parseGroupFromCipher("aasdfasdf");
        assertNull(groupNode);
    }
}
