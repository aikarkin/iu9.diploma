package ru.bmstu.schedule.dao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class HibernateDao<PK extends Serializable, E> implements Dao<PK, E> {
    private final Class<E> persistentClass;
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public HibernateDao(SessionFactory factory) {
        persistentClass = (Class<E>) ((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        sessionFactory = factory;
    }

    @Override
    public E findByKey(PK primaryKey) {
        return composeInTransaction(session ->
                session.get(persistentClass, primaryKey)
        );
    }

    @SuppressWarnings("unchecked")
    protected List<E> findAllByProperty(String propertyName, Object value) {
        return composeInTransaction(session -> {
            Criteria criteria = createEntityCriteria();
            criteria.add(Restrictions.eq(propertyName, value));
            return (List<E>) criteria.list();
        });
    }

    @SuppressWarnings("unchecked")
    protected Optional<E> findUniqueByProperty(String propertyName, Object value) {
        return composeInTransaction(session -> {
            Criteria criteria = createEntityCriteria();
            criteria.add(Restrictions.eq(propertyName, value));
            try {
                return Optional.ofNullable((E) criteria.uniqueResult());
            } catch (HibernateException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        });
    }

    protected List<E> filter(Predicate<E> predicate) {
        return findAll().stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<E> findAll() {
        return composeInTransaction(session ->
                (List<E>) createEntityCriteria().list()
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public PK create(E entity) {
        return composeInTransaction(session ->
                (PK)session.save(entity)
        );
    }

    @Override
    public void update(E entity) {
        consumeInTransaction(session -> session.update(entity));
    }

    @Override
    public void delete(E entity) {
        consumeInTransaction(session -> session.delete(entity));
    }

    protected void consumeInTransaction(Consumer<Session> consumer) {
        composeInTransaction(session -> {
            consumer.accept(session);
            return null;
        });
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    protected Criteria createEntityCriteria() {
        return getSession().createCriteria(persistentClass);
    }

    protected <T> T composeInTransaction(Function<Session, T> func) throws HibernateException {
        T entity;
        Session session = getSession();
        session.beginTransaction();

        entity = func.apply(session);

        session.getTransaction().commit();
        return entity;
    }


}
