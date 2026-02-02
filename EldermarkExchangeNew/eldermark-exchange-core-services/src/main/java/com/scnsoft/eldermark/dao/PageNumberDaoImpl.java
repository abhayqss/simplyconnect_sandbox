package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.intersector.RowNumberStatementInspector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Repository
public class PageNumberDaoImpl<ENTITY, SELECTOR> implements PageNumberDao<ENTITY, SELECTOR> {

    private final EntityManager entityManager;

    @Autowired
    public PageNumberDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Long findPageNumber(Function<Root<ENTITY>, Path<SELECTOR>> itemSelectorPath, SELECTOR itemSelectorValue, Specification<ENTITY> listSpecification, Class<ENTITY> entityClass, int pageSize, Sort sort) {
        var cb = entityManager.getCriteriaBuilder();
        var listQuery = cb.createQuery(Long.class);
        var root = listQuery.from(entityClass);

        var orders = QueryUtils.toOrders(sort, root, cb);

        List<Expression<?>> args = new ArrayList<>();

        args.add(itemSelectorPath.apply(root));
        args.add(cb.literal(itemSelectorValue));

        orders.forEach(order -> {
            args.add(order.getExpression());
            args.add(cb.literal(RowNumberStatementInspector.SortDirection.fromOrder(order).getCode()));
        });

        listQuery.multiselect(
                cb.function(RowNumberStatementInspector.ROW_NUMBER_FAKE_FUNCTION, Long.class, args.toArray(new Expression<?>[0]))
        );

        listQuery.where(listSpecification.toPredicate(root, listQuery, cb));

        var typed = entityManager.createQuery(listQuery);

        var result = typed.getResultList();
        if (result.isEmpty()) {
            return 0L;
        }
        return (result.get(0) - 1) / pageSize;
    }
}
