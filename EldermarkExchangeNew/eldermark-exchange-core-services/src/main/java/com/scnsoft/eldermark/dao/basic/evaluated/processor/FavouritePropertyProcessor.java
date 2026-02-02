package com.scnsoft.eldermark.dao.basic.evaluated.processor;

import com.scnsoft.eldermark.dao.basic.evaluated.params.EvaluatedPropertyParams;
import com.scnsoft.eldermark.dao.basic.evaluated.params.FavouritePropertyParams;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import java.util.Collection;

public class FavouritePropertyProcessor implements EvaluatedPropertyProcessor {

    @Override
    public <E> Expression<?> createExpression(Root<E> root, CriteriaQuery<?> crq, CriteriaBuilder cb,
                                              String propName,
                                              EvaluatedPropertyParams params) {
        var favouriteParams = (FavouritePropertyParams) params;

        var subQuery = crq.subquery(Integer.class);
        var subRoot = subQuery.from(favouriteParams.getEntityClass());
        var favourites = subRoot.<Collection<Long>>get(favouriteParams.getAddedAsFavouriteToEmployeeIdsAttr());
        subQuery.select(cb.literal(1))
                .where(cb.and(
                        cb.isMember(favouriteParams.getAddedToFavouriteByEmployeeId(), favourites),
                        cb.equal(subRoot, root)));

        return cb.selectCase().when(cb.exists(subQuery), cb.literal(1)).otherwise(cb.literal(0)).as(Boolean.class);
    }
}
