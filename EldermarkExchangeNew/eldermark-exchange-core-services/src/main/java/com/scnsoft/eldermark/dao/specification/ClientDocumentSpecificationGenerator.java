package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.InternalClientDocumentFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.ClientPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.CommunityPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.SecurityPredicateGenerator;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.document.ClientDocument;
import com.scnsoft.eldermark.entity.document.ClientDocument_;
import com.scnsoft.eldermark.entity.document.DocumentType;
import com.scnsoft.eldermark.entity.document.category.DocumentCategory_;
import com.scnsoft.eldermark.entity.lab.LabResearchOrderStatus;
import com.scnsoft.eldermark.entity.lab.LabResearchOrder_;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestStatus;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest_;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class ClientDocumentSpecificationGenerator {

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    @Autowired
    private SecurityPredicateGenerator securityPredicateGenerator;

    @Autowired
    private CommunityPredicateGenerator communityPredicateGenerator;

    public Specification<ClientDocument> byFilterAndMerged(InternalClientDocumentFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.getClientId() != null) {
                Predicate mergedClients = clientPredicateGenerator.clientAndMergedClients(criteriaBuilder,
                        JpaUtils.getOrCreateJoin(root, ClientDocument_.client),
                        criteriaQuery,
                        Collections.singletonList(filter.getClientId())
                );
                predicates.add(mergedClients);
            }
            if (StringUtils.isNotBlank(filter.getTitle())) {
                List<Predicate> searchPredicates = new ArrayList<>();
                var wrappedTitle = SpecificationUtils.wrapWithWildcards(filter.getTitle());
                searchPredicates.add(criteriaBuilder.like(root.get(ClientDocument_.documentTitle), wrappedTitle));
                searchPredicates.add(criteriaBuilder.like(root.get(ClientDocument_.documentTypeStr), wrappedTitle));
                if (filter.getIncludeSearchByCategoryName()) {
                    criteriaQuery.distinct(true);
                    var categoriesJoin = JpaUtils.getOrCreateListJoin(root, ClientDocument_.categories, JoinType.LEFT);
                    searchPredicates.add(criteriaBuilder.like(categoriesJoin.get(DocumentCategory_.name), wrappedTitle));
                }
                predicates.add(criteriaBuilder.or(searchPredicates.toArray(new Predicate[0])));
            }
            if (StringUtils.isNotBlank(filter.getDescription())) {
                var wrappedDescription = SpecificationUtils.wrapWithWildcards(filter.getDescription());
                predicates.add(criteriaBuilder.like(root.get(ClientDocument_.description), wrappedDescription));
            }
            if (CollectionUtils.isNotEmpty(filter.getCategoryIds()) || filter.getIncludeNotCategorized()) {
                var subQuery = criteriaQuery.subquery(Long.class);
                var subRoot = subQuery.from(ClientDocument.class);
                subQuery.select(subRoot.get(ClientDocument_.id));
                var joinCategoryIds = subRoot.join(ClientDocument_.categories, JoinType.LEFT).get(DocumentCategory_.id);
                var byCategories = criteriaBuilder.or();
                var notCategorized = criteriaBuilder.or();
                if (CollectionUtils.isNotEmpty(filter.getCategoryIds())) {
                    byCategories = joinCategoryIds.in(filter.getCategoryIds());
                }
                if (filter.getIncludeNotCategorized()) {
                    notCategorized = criteriaBuilder.isNull(joinCategoryIds);
                }
                subQuery.where(criteriaBuilder.or(byCategories, notCategorized));
                predicates.add(root.get(ClientDocument_.id).in(subQuery));
            }
            if (filter.getFromDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(ClientDocument_.creationTime), Instant.ofEpochMilli(filter.getFromDate())));
            }
            if (filter.getToDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(ClientDocument_.creationTime), Instant.ofEpochMilli(filter.getToDate())));
            }
            if (!filter.getIncludeDeleted()) {
                predicates.add(criteriaBuilder.isFalse(root.get(ClientDocument_.temporaryDeleted)));
            }
            if (CollectionUtils.isNotEmpty(filter.getSignatureStatuses()) || filter.getIncludeWithoutSignature()) {
                var signatureRequest = JpaUtils.getOrCreateJoin(root, ClientDocument_.signatureRequest, JoinType.LEFT);
                var signatureStatus = signatureRequest.get(DocumentSignatureRequest_.STATUS);

                var statusPredicates = new ArrayList<Predicate>();
                if (CollectionUtils.isNotEmpty(filter.getSignatureStatuses())) {
                    statusPredicates.add(signatureStatus.in(filter.getSignatureStatuses()));
                }
                if (filter.getIncludeWithoutSignature()) {
                    statusPredicates.add(criteriaBuilder.isNull(signatureRequest));
                }
                predicates.add(criteriaBuilder.or(statusPredicates.toArray(new Predicate[0])));
            }
            predicates.add(visible(root, criteriaBuilder));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<ClientDocument> hasAccess(PermissionFilter permissionFilter) {
        //documents from "not reviewed" lab orders are not available in client documents
        return hasRoleAccess(permissionFilter).and(excludeNotReviewedLabDocuments());
    }

    private Specification<ClientDocument> hasRoleAccess(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            var clientJoin = JpaUtils.getOrCreateJoin(root, ClientDocument_.client);
            var eligible = securityPredicateGenerator.clientInEligibleForDiscoveryCommunity(clientJoin, criteriaBuilder);
            var notCloud = criteriaBuilder.or(
                                criteriaBuilder.equal(root.get(ClientDocument_.isCloud), Boolean.FALSE),
                                root.get(ClientDocument_.isCloud).isNull());

            var predicates = new ArrayList<Predicate>();

            if (permissionFilter.hasPermission(Permission.DOCUMENT_VIEW_MERGED_SHARED_ALL_EXCEPT_OPTED_OUT)) {
                predicates.add(clientPredicateGenerator.isOptedOut(clientJoin, criteriaBuilder).not());
            }

            if (permissionFilter.hasPermission(Permission.DOCUMENT_VIEW_MERGED_SHARED_IF_ASSOCIATED_ORGANIZATION)) {
                var employees = permissionFilter.getEmployees(Permission.DOCUMENT_VIEW_MERGED_SHARED_IF_ASSOCIATED_ORGANIZATION);

                predicates.add(
                        criteriaBuilder.and(
                                isDocumentSharedWithAny(employees, root, criteriaBuilder, criteriaQuery),
                                securityPredicateGenerator.associatedOrganizationWithMergedPredicate(criteriaBuilder,
                                        clientJoin, criteriaQuery, employees)
                        )
                );
            }

            if (permissionFilter.hasPermission(Permission.DOCUMENT_VIEW_MERGED_SHARED_IF_ASSOCIATED_COMMUNITY)) {
                var employees = permissionFilter.getEmployees(Permission.DOCUMENT_VIEW_MERGED_SHARED_IF_ASSOCIATED_COMMUNITY);

                predicates.add(
                        criteriaBuilder.and(
                                isDocumentSharedWithAny(employees, root, criteriaBuilder, criteriaQuery),
                                securityPredicateGenerator.associatedCommunityWithMergedClients(criteriaBuilder,
                                        clientJoin, criteriaQuery, employees)
                        )
                );
            }

            if (permissionFilter.hasPermission(Permission.DOCUMENT_VIEW_MERGED_SHARED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION)) {
                var employees = permissionFilter.getEmployees(Permission.DOCUMENT_VIEW_MERGED_SHARED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION);

                predicates.add(
                        criteriaBuilder.and(
                                isDocumentSharedWithAny(employees, root, criteriaBuilder, criteriaQuery),
                                securityPredicateGenerator.primaryCommunitiesOfOrganizationsWithMergedClient(criteriaBuilder,
                                        clientJoin, criteriaQuery, employees),
                                clientPredicateGenerator.isOptedOut(clientJoin, criteriaBuilder).not()
                        )
                );
            }

            if (permissionFilter.hasPermission(Permission.DOCUMENT_VIEW_MERGED_SHARED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY)) {
                var employees = permissionFilter.getEmployees(Permission.DOCUMENT_VIEW_MERGED_SHARED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY);

                predicates.add(
                        criteriaBuilder.and(
                                isDocumentSharedWithAny(employees, root, criteriaBuilder, criteriaQuery),
                                securityPredicateGenerator.primaryCommunitiesWithMergedClient(criteriaBuilder,
                                        clientJoin, criteriaQuery, employees),
                                clientPredicateGenerator.isOptedOut(clientJoin, criteriaBuilder).not()
                        )
                );
            }

            if (permissionFilter.hasPermission(Permission.DOCUMENT_VIEW_MERGED_SHARED_IF_CURRENT_RP_COMMUNITY_CTM)) {
                var employees = permissionFilter.getEmployees(Permission.DOCUMENT_VIEW_MERGED_SHARED_IF_CURRENT_RP_COMMUNITY_CTM);

                predicates.add(
                        criteriaBuilder.and(
                                isDocumentSharedWithAny(employees, root, criteriaBuilder, criteriaQuery),
                                securityPredicateGenerator.communityCareTeamWithMergedPredicate(
                                        criteriaBuilder,
                                        clientJoin,
                                        criteriaQuery,
                                        employees,
                                        AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                                        HieConsentCareTeamType.current(clientJoin)
                                )
                        )
                );
            }

            if (permissionFilter.hasPermission(Permission.DOCUMENT_VIEW_MERGED_SHARED_IF_CURRENT_RP_CLIENT_CTM)) {
                var employees = permissionFilter.getEmployees(Permission.DOCUMENT_VIEW_MERGED_SHARED_IF_CURRENT_RP_CLIENT_CTM);

                predicates.add(
                        criteriaBuilder.and(
                                isDocumentSharedWithAny(employees, root, criteriaBuilder, criteriaQuery),
                                securityPredicateGenerator.clientCareTeamWithMergedPredicate(
                                        criteriaBuilder,
                                        clientJoin,
                                        criteriaQuery,
                                        employees,
                                        AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                                        HieConsentCareTeamType.current(clientJoin)
                                )
                        )
                );
            }

            if (permissionFilter.hasPermission(Permission.DOCUMENT_VIEW_MERGED_SHARED_IF_SELF_RECORD)) {
                var employees = permissionFilter.getEmployees(Permission.DOCUMENT_VIEW_MERGED_SHARED_IF_SELF_RECORD);

                predicates.add(
                        criteriaBuilder.and(
                                isDocumentSharedWithAny(employees, root, criteriaBuilder, criteriaQuery),
                                securityPredicateGenerator.selfRecordWithMergedPredicate(criteriaBuilder,
                                        clientJoin, criteriaQuery, employees)
                        )
                );
            }

            if (permissionFilter.hasPermission(Permission.DOCUMENT_VIEW_MERGED_SHARED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)) {
                var employees = permissionFilter.getEmployees(Permission.DOCUMENT_VIEW_MERGED_SHARED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);

                predicates.add(criteriaBuilder.and(
                        isDocumentSharedWithAny(employees, root, criteriaBuilder, criteriaQuery),
                        securityPredicateGenerator.addedByEmployeesWithMergedPredicate(criteriaBuilder, clientJoin,
                                criteriaQuery, employees),
                        clientPredicateGenerator.isOptedOut(clientJoin, criteriaBuilder).not()
                ));
            }

            if (permissionFilter.hasPermission(Permission.DOCUMENT_VIEW_MERGED_SHARED_IF_CLIENT_FOUND_IN_RECORD_SEARCH) &&
                    CollectionUtils.isNotEmpty(permissionFilter.getClientRecordSearchFoundIds())) {
                predicates.add(root.get(ClientDocument_.clientId).in(permissionFilter.getClientRecordSearchFoundIds()));
            }

            return criteriaBuilder.and(
                    eligible,
                    notCloud,
                    criteriaBuilder.or(predicates.toArray(new Predicate[0]))
            );
        };
    }

    private Specification<ClientDocument> excludeNotReviewedLabDocuments() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();
            var labResearchOrderJoin = root.join(ClientDocument_.labResearchOrder, JoinType.LEFT);
            predicates.add(criteriaBuilder.notEqual(root.get(ClientDocument_.documentType), DocumentType.LAB_RESULT));
            predicates.add(criteriaBuilder.equal(labResearchOrderJoin.get(LabResearchOrder_.status), LabResearchOrderStatus.REVIEWED));
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    private Predicate isDocumentSharedWithAny(Collection<Employee> employees, From<?, ClientDocument> documentFrom,
                                              CriteriaBuilder criteriaBuilder, CriteriaQuery<?> criteriaQuery) {
        var sharedSubquery = criteriaQuery.subquery(Integer.class);
        var subDocuments = sharedSubquery.from(ClientDocument.class);

        sharedSubquery.select(criteriaBuilder.literal(1))
                .where(
                        criteriaBuilder.and(
                                criteriaBuilder.equal(subDocuments, documentFrom),
                                criteriaBuilder.in(subDocuments.join(ClientDocument_.SHARED_WITH_ORGANIZATION_IDS, JoinType.INNER)).value(SpecificationUtils.employeesOrganizationIds(employees))

                        )
                );

        return criteriaBuilder.or(
                criteriaBuilder.equal(documentFrom.get(ClientDocument_.eldermarkShared), Boolean.TRUE),
                criteriaBuilder.exists(sharedSubquery)
        );
    }

    public Specification<ClientDocument> shouldBeSignedByEmployeeId(Long currentEmployeeId, Collection<Long> associatedClientIds) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var signatureRequest = JpaUtils.getOrCreateJoin(root, ClientDocument_.signatureRequest);
            var signatureStatus = signatureRequest.get(DocumentSignatureRequest_.STATUS);
            var requestedFromEmployeeId = signatureRequest.get(DocumentSignatureRequest_.requestedFromEmployeeId);
            var requestedFromClientId = signatureRequest.get(DocumentSignatureRequest_.requestedFromClientId);
            var expiresAt = signatureRequest.get(DocumentSignatureRequest_.dateExpires);

            return criteriaBuilder.and(
                    signatureStatus.in(DocumentSignatureRequestStatus.signatureRequestSentStatuses()),
                    criteriaBuilder.or(
                            criteriaBuilder.equal(requestedFromEmployeeId, currentEmployeeId),
                            SpecificationUtils.in(criteriaBuilder, requestedFromClientId, associatedClientIds)
                    ),
                    criteriaBuilder.greaterThan(expiresAt, Instant.now()),
                    visible(root, criteriaBuilder)
            );
        };
    }

    private Predicate visible(From<?, ClientDocument> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(
                criteriaBuilder.isNull(root.get(ClientDocument_.deletionTime)),
                criteriaBuilder.equal(root.get(ClientDocument_.visible), Boolean.TRUE)
        );
    }
}
