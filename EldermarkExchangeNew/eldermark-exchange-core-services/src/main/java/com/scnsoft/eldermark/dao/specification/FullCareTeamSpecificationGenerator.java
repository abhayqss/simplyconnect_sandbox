package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.dao.predicate.FullCareTeamPredicateGenerator;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class FullCareTeamSpecificationGenerator {

    @Autowired
    private FullCareTeamPredicateGenerator fullCareTeamPredicateGenerator;

    public Specification<CareTeamMember> careTeamMembersFromSameCareTeam(Collection<Long> sourceEmployeeIds,
                                                                         Long targetEmployeeId,
                                                                         HieConsentCareTeamType consentType) {
        return (root, query, criteriaBuilder) -> {
            var targetEmployeePath = root.get(CareTeamMember_.employeeId);
            return criteriaBuilder.and(
                    criteriaBuilder.equal(targetEmployeePath, targetEmployeeId),
                    fullCareTeamPredicateGenerator.fromSameCareTeam(sourceEmployeeIds, targetEmployeePath,
                            query, criteriaBuilder, consentType));
        };
    }
}
