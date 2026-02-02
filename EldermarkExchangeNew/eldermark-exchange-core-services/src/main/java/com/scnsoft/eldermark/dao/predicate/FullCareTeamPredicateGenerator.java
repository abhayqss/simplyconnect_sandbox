package com.scnsoft.eldermark.dao.predicate;

import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.dao.specification.SpecificationUtils;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.CommunityCareTeamMember;
import com.scnsoft.eldermark.entity.CommunityCareTeamMember_;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember_;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember_;
import com.scnsoft.eldermark.entity.client.MergedClientView;
import com.scnsoft.eldermark.entity.client.MergedClientView_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

@Component
public class FullCareTeamPredicateGenerator {

    @Autowired
    private CommunityCareTeamMemberPredicateGenerator communityCTMPredicates;

    @Autowired
    private ClientCareTeamMemberPredicateGenerator clientCTMPredicates;

    //todo - check performance
    //todo - not viewable care team?
    public Predicate fromSameCareTeam(Collection<Long> sourceEmployeeIds, Path<Long> targetEmployeeId,
                                      AbstractQuery<?> query, CriteriaBuilder criteriaBuilder,
                                      HieConsentCareTeamType consentType) {

        //prepare subqueries
        var sourceCommunitiesAsCommunityCtm = communityCtmSubquery(sourceEmployeeIds, query, criteriaBuilder, consentType);

        var sourceMergedClientsAsClientCtm = mergedClientCtmSubquery(sourceEmployeeIds,
                mergedClientRoot -> mergedClientRoot.get(MergedClientView_.mergedClientId),
                query, criteriaBuilder, consentType);

        var sourceMergedClientCommunitiesAsClientCtm = mergedClientCtmSubquery(sourceEmployeeIds,
                mergedClientRoot -> mergedClientRoot.join(MergedClientView_.mergedClient).get(Client_.communityId),
                query, criteriaBuilder, consentType);

        var predicates = new ArrayList<Predicate>(3);

        var subquery = query.subquery(Long.class);
        var communityCTMSubRoot = subquery.from(CommunityCareTeamMember.class);
        subquery = subquery.select(communityCTMSubRoot.get(CareTeamMember_.employeeId))
                .where(
                        criteriaBuilder.or(
                                //1. sourceCommunityCTM.community == targetCommunityCTM.community
                                communityCTMSubRoot.get(CommunityCareTeamMember_.communityId).in(sourceCommunitiesAsCommunityCtm),

                                //2. sourceClientCTM.client.merged.community == targetCommunityCTM.community
                                communityCTMSubRoot.get(CommunityCareTeamMember_.communityId).in(sourceMergedClientCommunitiesAsClientCtm)
                        ),
                        communityCTMPredicates.ofConsentType(communityCTMSubRoot, criteriaBuilder, query, consentType)
                );
        predicates.add(targetEmployeeId.in(subquery));


        //3. sourceCommunityCTM.community == targetClientCTM.client.merged.community
        subquery = targetMergedClientCtmSubquery(
                mergedClientSubRoot -> mergedClientSubRoot.join(MergedClientView_.mergedClient).get(Client_.communityId)
                        .in(sourceCommunitiesAsCommunityCtm),
                query, criteriaBuilder, consentType);
        predicates.add(targetEmployeeId.in(subquery));

        //4. sourceClientCTM.client.merged == targetClientCTM.client.merged
        subquery = targetMergedClientCtmSubquery(
                mergedClientSubRoot -> mergedClientSubRoot.get(MergedClientView_.mergedClientId).in(sourceMergedClientsAsClientCtm),
                query, criteriaBuilder, consentType);
        predicates.add(targetEmployeeId.in(subquery));

        //todo notOnHold?
        return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
    }

    private Subquery<Long> communityCtmSubquery(Collection<Long> sourceEmployeeIds, AbstractQuery<?> query, CriteriaBuilder criteriaBuilder,
                                                HieConsentCareTeamType consentCareTeamType) {
        var communitiesAsCommunityCtm = query.subquery(Long.class);
        var communityCtmRoot = communitiesAsCommunityCtm.from(CommunityCareTeamMember.class);
        return communitiesAsCommunityCtm
                .select(communityCtmRoot.get(CommunityCareTeamMember_.communityId))
                .where(
                        SpecificationUtils.in(criteriaBuilder, communityCtmRoot.get(CommunityCareTeamMember_.employeeId), sourceEmployeeIds),
                        communityCTMPredicates.ofConsentType(communityCtmRoot, criteriaBuilder, query, consentCareTeamType)
                );

    }

    private Subquery<Long> mergedClientCtmSubquery(Collection<Long> sourceEmployeeIds,
                                                   Function<Root<MergedClientView>, Expression<Long>> selector,
                                                   AbstractQuery<?> query, CriteriaBuilder criteriaBuilder,
                                                   HieConsentCareTeamType consentType) {
        var mergedClientsAsClientCtm = query.subquery(Long.class);
        var clientCtmRoot = mergedClientsAsClientCtm.from(ClientCareTeamMember.class);
        var mergedClientRoot = mergedClientsAsClientCtm.from(MergedClientView.class); //hopefully will be optimized to inner join by sql server
        return mergedClientsAsClientCtm
                .select(selector.apply(mergedClientRoot))
                .where(
                        criteriaBuilder.equal(clientCtmRoot.get(ClientCareTeamMember_.clientId), mergedClientRoot.get(MergedClientView_.clientId)),
                        SpecificationUtils.in(criteriaBuilder, clientCtmRoot.get(ClientCareTeamMember_.employeeId), sourceEmployeeIds),
                        clientCTMPredicates.ofConsentType(clientCtmRoot, criteriaBuilder, query, consentType)
                );
    }

    private Subquery<Long> targetMergedClientCtmSubquery(Function<Root<MergedClientView>, Predicate> whereClause,
                                                         AbstractQuery<?> query, CriteriaBuilder criteriaBuilder,
                                                         HieConsentCareTeamType consentType) {
        var subquery = query.subquery(Long.class);
        var clientCTMSubRoot = subquery.from(ClientCareTeamMember.class);
        var mergedClientSubRoot = subquery.from(MergedClientView.class);
        return subquery.select(clientCTMSubRoot.get(CareTeamMember_.employeeId))
                .where(
                        criteriaBuilder.equal(clientCTMSubRoot.get(ClientCareTeamMember_.clientId), mergedClientSubRoot.get(MergedClientView_.clientId)),
                        whereClause.apply(mergedClientSubRoot),
                        clientCTMPredicates.ofConsentType(clientCTMSubRoot, criteriaBuilder, query, consentType)
                );
    }

}
