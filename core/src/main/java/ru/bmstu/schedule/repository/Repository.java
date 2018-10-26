package ru.bmstu.schedule.repository;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Repository<E, K extends Serializable> {
    private SessionFactory sessionFactory;
    private Class<E> entityClass;

    public Repository(Class<E> entityClass, SessionFactory sessionFactory) {
        if(!entityClass.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Provided entity type is not annotated with @Entity");
        }

        this.entityClass = entityClass;
        this.sessionFactory = sessionFactory;
    }

    public List<E> getAll() {
        return composeTypedListInTransaction(session -> session.createCriteria(entityClass).list());
    }

    public E getFirst() {
        return getAll().size() == 0 ? null : getAll().get(0);
    }

    public E findById(K id) {
        return composeTypedValueInTransaction(session -> session.get(entityClass, id));
    }

    public E findExactByProperty(final String propertyName, final Object val) {
        return composeTypedValueInTransaction(session -> {
            Criteria criteria = session.createCriteria(entityClass);
            criteria.add(Restrictions.eq(propertyName, val));
            return (E)criteria.uniqueResult();
        });
    }

    public List<E> filterByProperty(final String propertyName, final Object val) {
        return composeTypedListInTransaction(session -> {
            Criteria criteria = session.createCriteria(entityClass);
            criteria.add(Restrictions.eq(propertyName, val));
            return (List<E>)criteria.list();
        });
    }

    public List<E> filter(Predicate<E> predicate) {
        return getAll().stream().filter(predicate).collect(Collectors.toList());
    }

    public Optional<E> findUniqie(Predicate<E> predicate) {
        List<E> found = filter(predicate);
        return found.size() > 0 ? Optional.of(found.get(0)) : Optional.empty();
    }

    public E create(E entity) {
        K id =  composeTypedValueInTransaction(session -> {
            K savedId = (K) session.save(entity);
            return savedId;
        });
        return findById(id);
    }

    public void update(E entity) {
        System.out.println("upd '" + entity + "'");
        doInTransaction(session -> {
            session.evict(entity);
            session.update(entity);
        });
//        K id = getIdOf(entity);

//        return id == null ? null : this.findById(id);
    }

    public boolean delete(E entity) {
        K id = getIdOf(entity);
        if(id == null)
            return false;

        E found = this.findById(id);
        if(found != null) {
            doInTransaction(session -> session.delete(entity));
            return true;
        }
        return false;
    }

    protected <T> T composeTypedValueInTransaction(Function<Session, T> func) throws HibernateException {
        T entity;
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        entity = func.apply(session);

        session.getTransaction().commit();
        session.close();

        return entity;
    }

    protected <T> List<T> composeTypedListInTransaction(Function<Session, List<T>> func) {
        final List<T> entitiesList = new ArrayList<>();

        doInTransaction(session -> entitiesList.addAll(func.apply(session)));

        return entitiesList;
    }

    protected void doInTransaction(Consumer<Session> action) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        action.accept(session);

        session.getTransaction().commit();
        session.flush();
        session.clear();
        session.close();
    }

    protected K getIdOf(E entity) {

        Method idGetter = getIdGetter();

        if(idGetter == null)
            return null;

        try {
            return (K)idGetter.invoke(entity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Method getIdGetter() {
        Method[] methods = entityClass.getMethods();
        Method idGetter = null;

        for(Method m : methods) {
            if(m.isAnnotationPresent(Id.class)) {
                idGetter = m;
                break;
            }
        }

        return idGetter;
    }
}
