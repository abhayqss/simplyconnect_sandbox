package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.DocumentSignatureTemplatePredicateGenerator;
import com.scnsoft.eldermark.entity.Organization_;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplate;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplate_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.JoinType;
import java.util.List;

@Component
public class DocumentSignatureTemplateSpecificationGenerator {

    @Autowired
    private DocumentSignatureTemplatePredicateGenerator templatePredicateGenerator;

    public Specification<DocumentSignatureTemplate> byPermissionFilterAndCommunityId(PermissionFilter permissionFilter, Long communityId) {
        return excludeDuplicates(
                hasAccess(permissionFilter)
                        .and(allowedForCommunityId(communityId))
        );
    }

    public Specification<DocumentSignatureTemplate> byPermissionFilterAndCommunityIds(PermissionFilter permissionFilter, List<Long> communityIds) {
        var hasAccess = hasAccess(permissionFilter);

        var spec = communityIds.stream()
                .map(this::allowedForCommunityId)
                .reduce(hasAccess, Specification::and);

        return excludeDuplicates(spec);
    }

    public Specification<DocumentSignatureTemplate> byIdAndCommunityId(Long id, Long communityId) {
        return excludeDuplicates(
                allowedForCommunityId(communityId)
                        .and(byId(id))
        );
    }

    private Specification<DocumentSignatureTemplate> excludeDuplicates(Specification<DocumentSignatureTemplate> spec) {
        return (root, query, cb) -> {
            var subQuery = query.subquery(Long.class);
            var subRoot = subQuery.from(DocumentSignatureTemplate.class);

            subQuery.distinct(true);

            subQuery.select(subRoot.get(DocumentSignatureTemplate_.id));
            subQuery.where(spec.toPredicate(subRoot, query, cb));

            return root.get(DocumentSignatureTemplate_.id).in(subQuery);
        };
    }

    public Specification<DocumentSignatureTemplate> manuallyCreated() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get(DocumentSignatureTemplate_.isManuallyCreated));
    }

    /**
     * Warning: adds duplicates
     */
    public Specification<DocumentSignatureTemplate> hasAccess(PermissionFilter permissionFilter) {
        return (root, query, cb) -> {
            var hasViewAccess = templatePredicateGenerator.hasViewAccess(permissionFilter, root, query, cb);
            var accessibleStatus = templatePredicateGenerator.accessibleStatus(permissionFilter, root, cb);
            return cb.and(hasViewAccess, accessibleStatus);
        };
    }

    /**
     * Warning: adds duplicates
     */
    public Specification<DocumentSignatureTemplate> allowedForCommunityId(Long communityId) {
        return (root, query, cb) -> {

            var turnedOnCommunity = JpaUtils.getOrCreateListJoin(
                    root,
                    DocumentSignatureTemplate_.communities,
                    JoinType.LEFT
            );

            var isSignatureEnabledForTurnedOnCommunity = JpaUtils.getOrCreateJoin(turnedOnCommunity, Community_.organization, JoinType.LEFT)
                    .get(Organization_.isSignatureEnabled);

            var turnedOnCommunityId = turnedOnCommunity.get(Community_.id);

            var turnedOnOrganization = JpaUtils.getOrCreateListJoin(
                    root,
                    DocumentSignatureTemplate_.organizations,
                    JoinType.LEFT
            );

            var turnedOnOrganizationId = turnedOnOrganization.get(Organization_.id);
            var isSignatureEnabledForTurnedOnOrganization = turnedOnOrganization.get(Organization_.isSignatureEnabled);

            var organizationIdOfCommunity = query.subquery(Long.class);
            var communityRoot = organizationIdOfCommunity.from(Community.class);
            organizationIdOfCommunity
                    .select(communityRoot.get(Community_.organizationId))
                    .where(cb.equal(communityRoot.get(Community_.id), communityId));

            return cb.or(
                    cb.and(
                            cb.equal(turnedOnCommunityId, communityId),
                            cb.isTrue(isSignatureEnabledForTurnedOnCommunity)
                    ),
                    cb.and(
                            cb.equal(turnedOnOrganizationId, organizationIdOfCommunity),
                            cb.isTrue(isSignatureEnabledForTurnedOnOrganization)
                    )
            );
        };
    }

    private Specification<DocumentSignatureTemplate> byId(Long templateId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(DocumentSignatureTemplate_.id), templateId);
    }
}
