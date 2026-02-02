package com.scnsoft.eldermark.dump.specification.predicate;

import com.scnsoft.eldermark.dump.entity.Client;
import com.scnsoft.eldermark.dump.entity.Client_;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

public class ClientPredicate {

    public static Predicate isActive(From<?, Client> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.isTrue(root.get(Client_.active));
    }
}
