package com.scnsoft.eldermark.dao.basic.evaluated.processor;

import com.scnsoft.eldermark.dao.basic.evaluated.params.EvaluatedPropertyParams;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

public interface EvaluatedPropertyProcessor {

    <E> Expression<?> createExpression(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder,
                                       String propName, EvaluatedPropertyParams params);

}
