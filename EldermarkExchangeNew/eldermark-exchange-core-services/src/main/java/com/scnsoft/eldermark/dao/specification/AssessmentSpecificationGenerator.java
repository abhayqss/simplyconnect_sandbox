package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.projection.OrganizationIdAware;
import com.scnsoft.eldermark.dao.predicate.AssessmentPredicateGenerator;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.entity.assessment.Assessment_;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class AssessmentSpecificationGenerator {

    @Autowired
    private AssessmentPredicateGenerator assessmentPredicateGenerator;

    public Specification<Assessment> typesAllowedInCommunity(OrganizationIdAware community) {
        return (root, criteriaQuery, criteriaBuilder) ->
                assessmentPredicateGenerator.typesAllowedInAnyCommunity(
                        Collections.singletonList(community),
                        root,
                        criteriaBuilder,
                        criteriaQuery
                );
    }

    public <T extends OrganizationIdAware> Specification<Assessment> typesAllowedInAnyCommunity(Collection<T> communities) {
        return (root, criteriaQuery, criteriaBuilder) ->
                assessmentPredicateGenerator.typesAllowedInAnyCommunity(
                        communities,
                        root,
                        criteriaBuilder,
                        criteriaQuery
                );
    }

    public Specification<Assessment> typesExistingForClients(Collection<Long> clientIds) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var subQuery = criteriaQuery.subquery(Long.class);
            var from = subQuery.from(ClientAssessmentResult.class);

            subQuery.select(from.get(ClientAssessmentResult_.assessmentId));
            subQuery.where(
                    SpecificationUtils.in(criteriaBuilder, from.get(ClientAssessmentResult_.clientId), clientIds)
            );

            return root.get(Assessment_.id).in(subQuery);
        };
    }

    public Specification<Assessment> byId(Long assessmentId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Assessment_.id), assessmentId);
    }

    public Specification<Assessment> byCodes(List<String> codes) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.in(root.get(Assessment_.CODE)).value(codes);
    }
}
