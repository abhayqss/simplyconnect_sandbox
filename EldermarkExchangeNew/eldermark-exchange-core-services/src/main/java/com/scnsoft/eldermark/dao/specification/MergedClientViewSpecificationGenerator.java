package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.entity.client.MergedClientView;
import com.scnsoft.eldermark.entity.client.MergedClientView_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class MergedClientViewSpecificationGenerator {

    public Specification<MergedClientView> mergedClientIdsAmong(Collection<Long> forClientIds, Collection<Long> amongClientIds) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                SpecificationUtils.in(criteriaBuilder, root.get(MergedClientView_.clientId), forClientIds),
                SpecificationUtils.in(criteriaBuilder, root.get(MergedClientView_.mergedClientId), amongClientIds)
        );
    }

}
