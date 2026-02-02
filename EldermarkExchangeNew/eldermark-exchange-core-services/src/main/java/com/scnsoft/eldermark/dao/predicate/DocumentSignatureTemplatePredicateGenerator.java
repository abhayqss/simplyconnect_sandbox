package com.scnsoft.eldermark.dao.predicate;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.entity.BaseEmployeeSecurityEntity;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Organization_;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplate;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplate_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.entity.security.Permission.*;
import static com.scnsoft.eldermark.entity.security.Permission.DOCUMENT_TEMPLATE_VIEW_IF_CO_RP_COMMUNITY_CTM;
import static com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateStatus.COMPLETED;
import static com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateStatus.DRAFT;

@Component
public class DocumentSignatureTemplatePredicateGenerator {

    @Autowired
    private SecurityPredicateGenerator securityPredicateGenerator;

    public Predicate accessibleStatus(PermissionFilter permissionFilter, Root<DocumentSignatureTemplate> root, CriteriaBuilder cb) {

        var templateStatus = root.get(DocumentSignatureTemplate_.status);

        if (permissionFilter.hasPermission(ROLE_SUPER_ADMINISTRATOR)) {
            return templateStatus.in(DRAFT, COMPLETED);
        }

        if (permissionFilter.hasPermission(DOCUMENT_TEMPLATE_MODIFY_IF_ASSOCIATED_ORGANIZATION)) {
            var organizationIds = permissionFilter.getEmployees(DOCUMENT_TEMPLATE_MODIFY_IF_ASSOCIATED_ORGANIZATION).stream()
                    .map(BasicEntity::getOrganizationId)
                    .collect(Collectors.toList());

            return cb.or(
                    cb.equal(templateStatus, COMPLETED),
                    cb.and(
                            cb.equal(templateStatus, DRAFT),
                            cb.or(
                                    turnedOnForAnyOrganization(organizationIds, root),
                                    turnedOnForAnyCommunityInOrganizations(organizationIds, root)
                            )
                    )
            );
        }

        return cb.equal(templateStatus, COMPLETED);
    }

    public Predicate hasViewAccess(PermissionFilter permissionFilter, Root<DocumentSignatureTemplate> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        var predicates = new ArrayList<Predicate>();

        if (permissionFilter.hasPermission(ROLE_SUPER_ADMINISTRATOR)) {
            return cb.conjunction();
        }

        if (permissionFilter.hasPermission(DOCUMENT_TEMPLATE_VIEW_IF_ASSOCIATED_ORGANIZATION)) {
            var organizationIds = permissionFilter.getEmployees(DOCUMENT_TEMPLATE_VIEW_IF_ASSOCIATED_ORGANIZATION).stream()
                    .map(BasicEntity::getOrganizationId)
                    .collect(Collectors.toList());

            predicates.add(turnedOnForAnyOrganization(organizationIds, root));
            predicates.add(turnedOnForAnyCommunityInOrganizations(organizationIds, root));
        }

        if (permissionFilter.hasPermission(DOCUMENT_TEMPLATE_VIEW_IF_ASSOCIATED_COMMUNITY)) {
            var communityIds = permissionFilter.getEmployees(DOCUMENT_TEMPLATE_VIEW_IF_ASSOCIATED_COMMUNITY).stream()
                    .map(BaseEmployeeSecurityEntity::getCommunityId)
                    .collect(Collectors.toList());

            predicates.add(turnedOnForAnyCommunity(communityIds, query, root, cb));
        }

        if (permissionFilter.hasPermission(DOCUMENT_TEMPLATE_VIEW_IF_CO_RP_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(DOCUMENT_TEMPLATE_VIEW_IF_CO_RP_COMMUNITY_CTM);
            predicates.add(turnedOnForAnyCommunityWhereEmployeeIsCoCtm(employees, query, root, cb, AffiliatedCareTeamType.REGULAR_AND_PRIMARY));
        }

        if (permissionFilter.hasPermission(DOCUMENT_TEMPLATE_VIEW_IF_CO_REGULAR_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(DOCUMENT_TEMPLATE_VIEW_IF_CO_REGULAR_COMMUNITY_CTM);
            predicates.add(turnedOnForAnyCommunityWhereEmployeeIsCoCtm(employees, query, root, cb, AffiliatedCareTeamType.REGULAR));
        }

        if (permissionFilter.hasPermission(DOCUMENT_TEMPLATE_VIEW_IF_CURRENT_RP_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(DOCUMENT_TEMPLATE_VIEW_IF_CURRENT_RP_CLIENT_CTM);
            predicates.add(turnedOnForAnyClientCommunityWhereEmployeeIsCurrentCtm(employees, query, root, cb, AffiliatedCareTeamType.REGULAR_AND_PRIMARY));
        }

        if (permissionFilter.hasPermission(DOCUMENT_TEMPLATE_VIEW_IF_CURRENT_REGULAR_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(DOCUMENT_TEMPLATE_VIEW_IF_CURRENT_REGULAR_CLIENT_CTM);
            predicates.add(turnedOnForAnyClientCommunityWhereEmployeeIsCurrentCtm(employees, query, root, cb, AffiliatedCareTeamType.REGULAR));
        }

        return cb.or(predicates.toArray(Predicate[]::new));
    }

    private Predicate turnedOnForAnyOrganization(Collection<Long> organizationIds, From<?, DocumentSignatureTemplate> root) {
        var organization = JpaUtils.getOrCreateListJoin(root, DocumentSignatureTemplate_.organizations, JoinType.LEFT);
        return organization.get(Organization_.id).in(organizationIds);
    }

    private Predicate turnedOnForAnyCommunityInOrganizations(Collection<Long> organizationIds, From<?, DocumentSignatureTemplate> root) {
        var community = JpaUtils.getOrCreateListJoin(root, DocumentSignatureTemplate_.communities, JoinType.LEFT);
        var communityOrganization = JpaUtils.getOrCreateJoin(community, Community_.organization, JoinType.LEFT);
        return communityOrganization.get(Organization_.id).in(organizationIds);
    }

    private Predicate turnedOnForAnyCommunity(Collection<Long> communityIds, AbstractQuery<?> query, From<?, DocumentSignatureTemplate> root, CriteriaBuilder cb) {
        var organization = JpaUtils.getOrCreateListJoin(root, DocumentSignatureTemplate_.organizations, JoinType.LEFT);
        var community = JpaUtils.getOrCreateListJoin(root, DocumentSignatureTemplate_.communities, JoinType.LEFT);

        var subQuery = query.subquery(Long.class);
        var subRoot = subQuery.from(Community.class);

        subQuery.select(subRoot.get(Community_.organizationId));
        subQuery.where(subRoot.get(Community_.id).in(communityIds));

        return cb.or(
                community.get(Community_.id).in(communityIds),
                organization.get(Organization_.id).in(subQuery)
        );
    }

    private Predicate turnedOnForAnyCommunityWhereEmployeeIsCoCtm(
            Collection<Employee> employees,
            AbstractQuery<?> query,
            From<?, DocumentSignatureTemplate> root,
            CriteriaBuilder cb,
            AffiliatedCareTeamType type
    ) {

        var subCommunityQuery = query.subquery(Long.class);
        var subCommunityRoot = subCommunityQuery.from(Community.class);

        subCommunityQuery.select(subCommunityRoot.get(Community_.id));
        subCommunityQuery.where(securityPredicateGenerator.communityIdsInCommunityCareTeamPredicate(
                cb,
                query,
                subCommunityRoot.get(Community_.id),
                employees,
                type,
                HieConsentCareTeamType.currentAndOnHold()
        ));

        var organization = JpaUtils.getOrCreateListJoin(root, DocumentSignatureTemplate_.organizations, JoinType.LEFT);
        var community = JpaUtils.getOrCreateListJoin(root, DocumentSignatureTemplate_.communities, JoinType.LEFT);

        var subQuery = query.subquery(Long.class);
        var subRoot = subQuery.from(Community.class);

        subQuery.select(subRoot.get(Community_.organizationId));
        subQuery.where(subRoot.get(Community_.id).in(subCommunityQuery));

        return cb.or(
                community.get(Community_.id).in(subCommunityQuery),
                organization.get(Organization_.id).in(subQuery)
        );
    }

    private Predicate turnedOnForAnyClientCommunityWhereEmployeeIsCurrentCtm(
            Collection<Employee> employees,
            AbstractQuery<?> query,
            From<?, DocumentSignatureTemplate> root,
            CriteriaBuilder cb,
            AffiliatedCareTeamType type
    ) {

        var clientCommunitySubQuery = query.subquery(Long.class);
        var clientSubRoot = clientCommunitySubQuery.from(Client.class);

        clientCommunitySubQuery.select(clientSubRoot.get(Community_.id));
        clientCommunitySubQuery.where(securityPredicateGenerator.clientsInClientCareTeamPredicate(
                cb,
                query,
                clientSubRoot,
                employees,
                type,
                HieConsentCareTeamType.current(clientSubRoot)
        ));

        var organization = JpaUtils.getOrCreateListJoin(root, DocumentSignatureTemplate_.organizations, JoinType.LEFT);
        var community = JpaUtils.getOrCreateListJoin(root, DocumentSignatureTemplate_.communities, JoinType.LEFT);

        var communityIdSubQuery = query.subquery(Long.class);
        var communitySubRoot = communityIdSubQuery.from(Community.class);

        communityIdSubQuery.select(communitySubRoot.get(Community_.organizationId));
        communityIdSubQuery.where(communitySubRoot.get(Community_.id).in(clientCommunitySubQuery));

        return cb.or(
                community.get(Community_.id).in(clientCommunitySubQuery),
                organization.get(Organization_.id).in(communityIdSubQuery)
        );
    }
}
