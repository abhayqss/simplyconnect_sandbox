package com.scnsoft.eldermark.dao.specification;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.Set;

/**
 * Wraps {@link Subquery} to implement {@link CriteriaQuery} interface
 * to make it possible to use with {@link Specification}
 */
class CriteriaSubqueryWrapper<T> implements CriteriaQuery<T> {

    private final Subquery<T> subquery;

    public static <T> CriteriaQuery<T> wrap(Subquery<T> subquery) {
        return new CriteriaSubqueryWrapper<>(subquery);
    }

    private CriteriaSubqueryWrapper(Subquery<T> subquery) {
        this.subquery = subquery;
    }

    @Override
    public CriteriaQuery<T> where(Expression<Boolean> restriction) {
        subquery.where(restriction);
        return this;
    }

    @Override
    public CriteriaQuery<T> where(Predicate... restrictions) {
        subquery.where(restrictions);
        return this;
    }

    @Override
    public CriteriaQuery<T> groupBy(Expression<?>... grouping) {
        subquery.groupBy(grouping);
        return this;
    }

    @Override
    public CriteriaQuery<T> groupBy(List<Expression<?>> grouping) {
        subquery.groupBy(grouping);
        return this;
    }

    @Override
    public CriteriaQuery<T> having(Expression<Boolean> restriction) {
        subquery.having(restriction);
        return this;
    }

    @Override
    public CriteriaQuery<T> having(Predicate... restrictions) {
        subquery.having(restrictions);
        return this;
    }

    @Override
    public CriteriaQuery<T> distinct(boolean distinct) {
        subquery.distinct(distinct);
        return this;
    }

    @Override
    public CriteriaQuery<T> select(Selection<? extends T> selection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CriteriaQuery<T> multiselect(Selection<?>... selections) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CriteriaQuery<T> multiselect(List<Selection<?>> selectionList) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <X> Root<X> from(Class<X> entityClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <X> Root<X> from(EntityType<X> entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CriteriaQuery<T> orderBy(Order... o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CriteriaQuery<T> orderBy(List<Order> o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Root<?>> getRoots() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Selection<T> getSelection() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Expression<?>> getGroupList() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Predicate getGroupRestriction() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDistinct() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<T> getResultType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Order> getOrderList() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<ParameterExpression<?>> getParameters() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <U> Subquery<U> subquery(Class<U> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Predicate getRestriction() {
        throw new UnsupportedOperationException();
    }
}
