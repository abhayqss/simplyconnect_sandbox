package com.scnsoft.eldermark.dao;

import java.io.Serializable;
import java.util.List;

/**
 * The definition of the Database Access Objects that handle the reading and writing a class from the database.
 * Created by pzhurba on 23-Sep-15.
 */
public interface BaseDao<T extends Serializable> {
    List<T> list(String orderBy);
    T get(Long id);
    T create(T entity);
    List<T> create(Iterable<T> entities);
    T merge(T entity);
    void delete(T entity);
    void delete(Long id);
    void flush();
    @SuppressWarnings("TypeParameterHidesVisibleType")
    <T>void detach(T entity);
}
