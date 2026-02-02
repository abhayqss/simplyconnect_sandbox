package com.scnsoft.eldermark.dump.specification;

import com.scnsoft.eldermark.dump.entity.Client;
import com.scnsoft.eldermark.dump.entity.Client_;
import com.scnsoft.eldermark.dump.entity.Community;
import com.scnsoft.eldermark.dump.entity.Organization;
import com.scnsoft.eldermark.dump.entity.assessment.AssessmentStatus;
import com.scnsoft.eldermark.dump.entity.assessment.Assessment_;
import com.scnsoft.eldermark.dump.entity.assessment.ClientAssessmentResult;
import com.scnsoft.eldermark.dump.entity.assessment.ClientAssessmentResult_;
import com.scnsoft.eldermark.dump.specification.predicate.AuditableEntityPredicateGenerator;
import com.scnsoft.eldermark.dump.specification.predicate.ClientAssessmentResultPredicateGenerator;
import com.scnsoft.eldermark.dump.specification.predicate.ClientPredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
public class ClientAssessmentResultSpecificationGenerator extends AuditableEntitySpecificationGenerator<ClientAssessmentResult> {

    private static final String GAD7 = "GAD-7";
    private static final String PHQ9 = "PHQ-9";
    private static final String COMPREHENSIVE = "Comprehensive Assessment";

    @Autowired
    private ClientAssessmentResultPredicateGenerator clientAssessmentResultPredicateGenerator;

    @Autowired
    private AuditableEntityPredicateGenerator auditableEntityPredicateGenerator;

    public Specification<ClientAssessmentResult> gad7CompletedByOrganizationId(Long organizationId) {
        return unarchivedByOrganizationIdAndTypeAndCompleted(organizationId, GAD7);
    }

    public Specification<ClientAssessmentResult> phq9CompletedByOrganizationId(Long organizationId) {
        return unarchivedByOrganizationIdAndTypeAndCompleted(organizationId, PHQ9);
    }

    public Specification<ClientAssessmentResult> comprehensiveCompletedByOrganizationId(Long organizationId) {
        return unarchivedByOrganizationIdAndTypeAndCompleted(organizationId, COMPREHENSIVE);
    }

    public Specification<ClientAssessmentResult> gad7CompletedByCommunity(Community community) {
        return unarchivedByCommunityAndTypeAndCompleted(community, GAD7);
    }

    public Specification<ClientAssessmentResult> phq9CompletedByCommunity(Community community) {
        return unarchivedByCommunityAndTypeAndCompleted(community, PHQ9);
    }

    public Specification<ClientAssessmentResult> comprehensiveCompletedByCommunity(Community community) {
        return unarchivedByCommunityAndTypeAndCompleted(community, COMPREHENSIVE);
    }

    public Specification<ClientAssessmentResult> comprehensiveCompleted(List<Long> clientIds) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.in(root.join(ClientAssessmentResult_.client).get(Client_.ID)).value(clientIds),
                        criteriaBuilder.equal(root.join(ClientAssessmentResult_.assessment).get(Assessment_.shortName), COMPREHENSIVE),
                        unarchived(root, criteriaBuilder),
                        criteriaBuilder.equal(root.get(ClientAssessmentResult_.assessmentStatus), AssessmentStatus.COMPLETED)
                );
    }

    private Specification<ClientAssessmentResult> unarchivedByOrganizationIdAndTypeAndCompleted(Long organizationId, String shortName) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.join(ClientAssessmentResult_.client).get(Client_.organizationId), organizationId),
                        unarchivedByTypeAndCompleted(shortName, root, criteriaBuilder)
                );
    }

    private Specification<ClientAssessmentResult> unarchivedByCommunityAndTypeAndCompleted(Community community, String shortName) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.join(ClientAssessmentResult_.client).get(Client_.community), community),
                        unarchivedByTypeAndCompleted(shortName, root, criteriaBuilder)
                );
    }

    private Predicate unarchivedByTypeAndCompleted(String shortName, From<?, ClientAssessmentResult> from, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(
                unarchived(from, criteriaBuilder),
                criteriaBuilder.equal(from.join(ClientAssessmentResult_.assessment).get(Assessment_.shortName), shortName),
                criteriaBuilder.equal(from.get(ClientAssessmentResult_.assessmentStatus), AssessmentStatus.COMPLETED)
        );
    }

    public Specification<ClientAssessmentResult> gad7OfClient(Client client) {
        return clientAssessmentResultByClientAndType(client, GAD7);
    }

    public Specification<ClientAssessmentResult> phq9OfClient(Client client) {
        return clientAssessmentResultByClientAndType(client, PHQ9);
    }

    private Specification<ClientAssessmentResult> clientAssessmentResultByClientAndType(Client client, String shortName) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get(ClientAssessmentResult_.client), client),
                        criteriaBuilder.equal(root.join(ClientAssessmentResult_.assessment).get(Assessment_.shortName), shortName),
                        unarchived(root, criteriaBuilder)
                );
    }

    public Specification<ClientAssessmentResult> ofActiveClients() {
        return (root, criteriaQuery, criteriaBuilder) ->
                ClientPredicate.isActive(root.join(ClientAssessmentResult_.client), criteriaBuilder);
    }

    public Specification<ClientAssessmentResult> withinReportPeriod(LocalDateTime start, LocalDateTime end) {
        return inProgressTillDate(end).or(completedWithinPeriod(start, end));
    }

    public Specification<ClientAssessmentResult> inProgressTillDate(LocalDateTime end) {
        Objects.requireNonNull(end);
        return (root, criteriaQuery, criteriaBuilder) -> clientAssessmentResultPredicateGenerator.inProgressTillDate(end, root, criteriaBuilder);
    }

    public Specification<ClientAssessmentResult> completedWithinPeriod(LocalDateTime start, LocalDateTime end) {
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);
        return (root, criteriaQuery, criteriaBuilder) -> clientAssessmentResultPredicateGenerator.completedWithinPeriod(start, end, root, criteriaBuilder);
    }

    public Specification<ClientAssessmentResult> leaveLatest(LocalDateTime till) {
        return (root, query, criteriaBuilder) -> auditableEntityPredicateGenerator.leaveLatest(ClientAssessmentResult.class, till, root, query, criteriaBuilder);
    }

    public Specification<ClientAssessmentResult> comprehensiveType() {
        return byType(COMPREHENSIVE);
    }

    public Specification<ClientAssessmentResult> byType(String shortName) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.join(ClientAssessmentResult_.assessment).get(Assessment_.shortName), shortName);
    }

    public Specification<ClientAssessmentResult> ofCommunity(Community community) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (community == null) {
                return criteriaBuilder.or();
            }
            return criteriaBuilder.equal(root.get(ClientAssessmentResult_.client).get(Client_.community), community);
        };
    }

    public Specification<ClientAssessmentResult> ofOrganizationId(Long organizationId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (organizationId == null) {
                return criteriaBuilder.or();
            }
            return criteriaBuilder.equal(root.get(ClientAssessmentResult_.client).get(Client_.organizationId), organizationId);
        };
    }

}
