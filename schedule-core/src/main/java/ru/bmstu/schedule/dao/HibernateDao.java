package ru.bmstu.schedule.dao;

import org.hibernate.*;
import org.hibernate.criterion.Restrictions;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class HibernateDao<PK extends Serializable, E> implements Dao<PK, E> {

    private final Class<E> persistentClass;
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public HibernateDao(SessionFactory factory) {
        persistentClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        sessionFactory = factory;
    }

    public Class<E> getPersistentClass() {
        return persistentClass;
    }

    @Override
    public Optional<E> findByKey(PK primaryKey) {
        try {
            return Optional.of(composeInTransaction(session ->
                    session.get(persistentClass, primaryKey)
            ));
        } catch (HibernateException e) {
            return Optional.empty();
        }
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
                (PK) session.save(entity)
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

    @Override
    public void deleteAll() {
        consumeInTransaction(session -> {
            Query query = session.createQuery("delete from " + getPersistentClass().getSimpleName());
            query.executeUpdate();
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

    protected Session getSession() {
        Session session = sessionFactory.getCurrentSession();
        return session.isOpen() ? session : sessionFactory.openSession();
    }

    Criteria createEntityCriteria() {
        return getSession().createCriteria(persistentClass);
    }

    protected <T> T composeInTransaction(Function<Session, T> func) throws HibernateException {
        Session session = getSession();
        T entity;
        try {
            session.beginTransaction();
            entity = func.apply(session);
            session.getTransaction().commit();
        } catch (Exception e){
            session.getTransaction().rollback();
            session.clear();
            throw e;
        }
        return entity;
    }

    private void consumeInTransaction(Consumer<Session> consumer) {
        composeInTransaction(session -> {
            consumer.accept(session);
            return null;
        });
    }


}
