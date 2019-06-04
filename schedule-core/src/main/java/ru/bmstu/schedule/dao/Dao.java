package ru.bmstu.schedule.dao;

import java.io.Serializable;
import java.util.Collection;

public interface Dao<PK extends Serializable, E> {

    Collection<E> findAll();

    E findByKey(PK key);

    PK create(E entity);

    void update(E entity);

    void delete(E entity);

}
