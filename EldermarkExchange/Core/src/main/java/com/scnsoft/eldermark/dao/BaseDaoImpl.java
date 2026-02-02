package com.scnsoft.eldermark.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for the Database Access Objects that handle the reading and writing a class from the database.
 * Created by pzhurba on 23-Sep-15.
 */
public class BaseDaoImpl<T extends Serializable> implements BaseDao<T> {
    final protected Class<T> entityClass;

    public BaseDaoImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @PersistenceContext
    protected EntityManager entityManager;

    @Override
    public List<T> list(String orderBy) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> rootEntry = cq.from(entityClass);
        cq.select(rootEntry);
        if (orderBy != null) {
           cq.orderBy(cb.asc(rootEntry.get(orderBy)));
        }
        TypedQuery<T> allQuery = entityManager.createQuery(cq);
        return allQuery.getResultList();
    }

    @Override
    public T get(Long id) {
        return entityManager.find(entityClass, id);
    }

    @Override
    public T create(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public List<T> create(Iterable<T> entities) {
        final List<T> result = new ArrayList<>();
        if (entities == null) {
            return result;
        }

        for (T entity : entities) {
            result.add(create(entity));
        }

        return result;
    }

    @Override
    public T merge(T entity) {
        T result = entityManager.merge(entity);
        return result;
    }

    public void flush() {
        entityManager.flush();
    }

    protected void applyPageable(final Query query, final Pageable pageRequest) {
        if (pageRequest != null) {
            query.setMaxResults(pageRequest.getPageSize());
            query.setFirstResult(pageRequest.getOffset());
        }
    }

    @Override
    public void delete(Long id) {
        entityManager.remove(get(id));
    }

    @SuppressWarnings("TypeParameterHidesVisibleType")
    @Override
    public <T>void detach(T entity) {
        entityManager.detach(entity);
    }

    @Override
    public void delete(T entity) {
        entityManager.remove(entity);
    }

    protected boolean addSort(StringBuilder sb, Sort.Order order, String field, boolean multiColumnSort) {
        if (order != null && field != null) {
            sb.append(multiColumnSort? ", " : " ORDER BY ");
            sb.append(field);
            sb.append(" ");
            sb.append(order.getDirection());
            return true;
        }
        return false;
    }

}
