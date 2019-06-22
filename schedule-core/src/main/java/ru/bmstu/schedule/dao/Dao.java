package ru.bmstu.schedule.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

public interface Dao<PK extends Serializable, E> {

    Collection<E> findAll();

    Optional<E> findByKey(PK key);

    PK create(E entity);

    void update(E entity);

    void delete(E entity);

    void deleteAll();

}
