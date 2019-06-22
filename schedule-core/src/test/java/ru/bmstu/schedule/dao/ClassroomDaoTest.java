package ru.bmstu.schedule.dao;

import org.junit.jupiter.api.*;
import ru.bmstu.schedule.entity.Classroom;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClassroomDaoTest extends DatabaseAccessTest {

    private static ClassroomDao dao;

    private static final String ROOM_NUMBER = "numberA";
    private static final String NEW_ROOM_NUMBER = "numberB";

    @BeforeAll
    static void setUp() {
        dao = new ClassroomDao(getSessionFactory());
    }

    @Test
    @Order(1)
    void testCreate() {
        System.out.println("1");
        Integer roomAId = dao.create(new Classroom(ROOM_NUMBER, 10));

        Optional<Classroom> roomAOpt = dao.findByKey(roomAId);

        assertTrue(roomAOpt.isPresent());

        assertEquals(roomAOpt.get().getCapacity(), 10);
        assertEquals(roomAOpt.get().getRoomNumber(), ROOM_NUMBER);
    }

    @Test
    @Order(2)
    void testUpdate() {
        Optional<Classroom> roomOpt = dao.findByRoomNumber(ROOM_NUMBER);
        assertTrue(roomOpt.isPresent());
        Classroom room = roomOpt.get();

        room.setCapacity(20);
        room.setRoomNumber(NEW_ROOM_NUMBER);
        dao.update(room);

        Optional<Classroom> roomBOpt = dao.findByRoomNumber(NEW_ROOM_NUMBER);
        assertFalse(dao.findByRoomNumber(ROOM_NUMBER).isPresent());
        assertTrue(roomBOpt.isPresent());
        assertEquals(roomBOpt.get().getRoomNumber(), NEW_ROOM_NUMBER);
        assertEquals(roomBOpt.get().getCapacity(), 20);
    }

    @Test
    @Order(3)
    void testDelete() {
        Optional<Classroom> roomOpt = dao.findByRoomNumber(NEW_ROOM_NUMBER);
        assertTrue(roomOpt.isPresent());
        dao.delete(roomOpt.get());
        assertFalse(dao.findByRoomNumber(NEW_ROOM_NUMBER).isPresent());
    }

    @AfterAll
    static void tearsDown() {
        dao.findByRoomNumber(NEW_ROOM_NUMBER).ifPresent(dao::delete);
        dao.findByRoomNumber(ROOM_NUMBER).ifPresent(dao::delete);
    }

}