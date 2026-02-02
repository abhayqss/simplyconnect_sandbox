package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.predicate.CareTeamMemberPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.CommunityCareTeamMemberPredicateGenerator;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.CommunityCareTeamMember;
import com.scnsoft.eldermark.entity.CommunityCareTeamMember_;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.From;
import java.util.Collection;
import java.util.function.Function;

@Component
public class CommunityCareTeamMemberSpecificationGenerator extends CareTeamMemberSpecificationGenerator<CommunityCareTeamMember> {

    @Autowired
    private CommunityCareTeamMemberPredicateGenerator communityCareTeamMemberPredicateGenerator;

    public Specification<CommunityCareTeamMember> byCommunityId(Long communityId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (communityId == null) {
                return criteriaBuilder.or();
            }
            return criteriaBuilder.equal(root.get(CommunityCareTeamMember_.communityId), communityId);
        };
    }

    public Specification<CommunityCareTeamMember> byCommunityIn(Collection<Community> communities) {
        return byCommunityIdIn(CareCoordinationUtils.toIdsSet(communities));
    }

    public Specification<CommunityCareTeamMember> byCommunityIdIn(Collection<Long> communityIds) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (CollectionUtils.isEmpty(communityIds)) {
                return criteriaBuilder.or();
            }
            return criteriaBuilder.in(root.get(CommunityCareTeamMember_.COMMUNITY_ID)).value(communityIds);
        };
    }

    public Specification<CommunityCareTeamMember> byCommunityIdNameIn(Collection<IdNameAware> communities) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (CollectionUtils.isEmpty(communities)) {
                return criteriaBuilder.or();
            }
            var communityIds = CareCoordinationUtils.toIdsSet(communities);
            return criteriaBuilder.in(root.get(CommunityCareTeamMember_.COMMUNITY_ID)).value(communityIds);
        };
    }

    public Specification<CommunityCareTeamMember> hasAccess(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> communityCareTeamMemberPredicateGenerator.hasAccess(permissionFilter, root, criteriaQuery, criteriaBuilder);
    }

    public Specification<CommunityCareTeamMember> byCommunityOrganizationId(Long organizationId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
                root.get(CommunityCareTeamMember_.community).get(Community_.organizationId), organizationId
        );
    }

    public Specification<CommunityCareTeamMember> byCommunityOrganizationIdIn(Collection<Long> organizationIds) {
        return (root, query, criteriaBuilder) -> root.get(CommunityCareTeamMember_.community).get(Community_.organizationId).in(organizationIds);
    }

    @Override
    protected CareTeamMemberPredicateGenerator<CommunityCareTeamMember> getPredicateGenerator() {
        return communityCareTeamMemberPredicateGenerator;
    }

    public Specification<CommunityCareTeamMember> onHoldCandidates() {
        //"ON Hold" = just affiliated from community ctm
        return ofAffiliationType(AffiliatedCareTeamType.PRIMARY);
    }

    public Specification<CommunityCareTeamMember> notOnHoldCandidates() {
        //"ON Hold" = just affiliated from community ctm
        return ofAffiliationType(AffiliatedCareTeamType.REGULAR);
    }

    public Specification<CommunityCareTeamMember> ofConsentType(HieConsentCareTeamType consentType) {
        return (root, criteriaQuery, criteriaBuilder) -> communityCareTeamMemberPredicateGenerator.ofConsentType(
                root,
                criteriaBuilder,
                criteriaQuery,
                consentType);
    }

    public Specification<CommunityCareTeamMember> ofConsentTypeInCommunity(Long communityId, Function<From<?, Client>, HieConsentCareTeamType> consentType) {
        return (root, query, criteriaBuilder) -> {

            var subQuery = query.subquery(Long.class);
            var subRoot = subQuery.from(Client.class);
            subQuery.where(criteriaBuilder.equal(subRoot.get(Client_.communityId), communityId));

            return ofConsentType(consentType.apply(subRoot))
                    .toPredicate(root, query, criteriaBuilder);
        };
    }
}
