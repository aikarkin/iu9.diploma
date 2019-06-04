package ru.bmstu.schedule.dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class DatabaseAccessTest {

    private static SessionFactory sessionFactory;

    @BeforeAll
    public static void beforeTestsStarted() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @AfterAll
    public static void afterTestsFinished() {
        sessionFactory.close();
    }

    protected static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}
