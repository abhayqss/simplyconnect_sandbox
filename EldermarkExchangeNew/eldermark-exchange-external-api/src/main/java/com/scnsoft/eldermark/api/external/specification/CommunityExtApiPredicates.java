package com.scnsoft.eldermark.api.external.specification;

import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.Community_;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

@Component
class CommunityExtApiPredicates {

    Predicate isCommunityConsanaSyncEnabled(Path<Community> path, CriteriaBuilder cb, boolean value) {
        return cb.equal(path.get(Community_.isConsanaIntegrationEnabled), value);
    }
}
