package com.scnsoft.eldermark.dao.predicate;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.ExternalEmployeeInboundReferralCommunity;
import com.scnsoft.eldermark.entity.ExternalEmployeeInboundReferralCommunity_;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.Collection;

@Component
public class ExternalEmployeeInboundReferralCommunityPredicateGenerator {

    public Predicate communityIdsInReferralSharedCommunities(Path<Long> communityIdPath, AbstractQuery query, Collection<Employee> employees) {
        var communityIds = query.subquery(Long.class);
        var externalRoot = communityIds.from(ExternalEmployeeInboundReferralCommunity.class);
        communityIds.select(externalRoot.get(ExternalEmployeeInboundReferralCommunity_.communityId));
        communityIds.where(externalRoot.get(ExternalEmployeeInboundReferralCommunity_.employee).in(employees));
        return communityIdPath.in(communityIds);
    }
}
