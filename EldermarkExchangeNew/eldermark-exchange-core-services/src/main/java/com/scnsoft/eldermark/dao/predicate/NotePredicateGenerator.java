package com.scnsoft.eldermark.dao.predicate;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.note.EncounterNote;
import com.scnsoft.eldermark.entity.note.EncounterNote_;
import com.scnsoft.eldermark.entity.note.Note;
import com.scnsoft.eldermark.entity.note.Note_;
import com.scnsoft.eldermark.entity.security.Permission;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.time.Instant;
import java.util.ArrayList;

@Component
public class NotePredicateGenerator {

    @Autowired
    private SecurityPredicateGenerator securityPredicateGenerator;

    @Autowired
    private EventPredicateGenerator eventPredicateGenerator;

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    public Predicate from(Instant dateFrom, CriteriaBuilder criteriaBuilder, Path<EncounterNote> root) {
        if (dateFrom != null)
            return criteriaBuilder.greaterThanOrEqualTo(root.get(EncounterNote_.encounterFromTime), dateFrom);
        return criteriaBuilder.or();
    }

    public Predicate to(Instant dateTo, CriteriaBuilder criteriaBuilder, Path<EncounterNote> root) {
        if (dateTo != null)
            return criteriaBuilder.lessThanOrEqualTo(root.get(EncounterNote_.encounterToTime), dateTo);
        return criteriaBuilder.or();
    }

    public <N extends Note> Predicate hasAccessAndDistinct(PermissionFilter permissionFilter, From<?, N> noteFrom,
                                                           AbstractQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        var clientJoin = JpaUtils.getOrCreateListJoin(noteFrom, Note_.noteClients);
        criteriaQuery.distinct(true); //many to many join to client

        return hasAccess(permissionFilter, noteFrom, clientJoin, criteriaQuery, criteriaBuilder);
    }

    public <N extends Note> Predicate hasAccessAndDistinctIgnoringNotViewable(PermissionFilter permissionFilter, From<?, N> noteFrom,
                                                                              AbstractQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        var clientJoin = JpaUtils.getOrCreateListJoin(noteFrom, Note_.noteClients);
        criteriaQuery.distinct(true); //many to many join to client

        return hasAccess(permissionFilter, noteFrom, clientJoin, criteriaQuery, criteriaBuilder, true);
    }

    //passing clientJoin as separate parameter for better EventNotes performance - we already have join to clients there
    public <N extends Note> Predicate hasAccess(PermissionFilter permissionFilter, From<?, N> noteFrom, From<?, Client> clientJoin,
                                                AbstractQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

        return hasAccess(permissionFilter, noteFrom, clientJoin, criteriaQuery, criteriaBuilder, false);
    }

    private <N extends Note> Predicate hasAccess(PermissionFilter permissionFilter, From<?, N> noteFrom, From<?, Client> clientJoin,
                                                 AbstractQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder, boolean ignoreNotViewable) {
        var permissionPredicate = permissionPredicateOnlyByClient(permissionFilter, clientJoin, criteriaQuery, criteriaBuilder);

        if (ignoreNotViewable) {
            return permissionPredicate;
        } else {
            var eventJoin = noteFrom.join(Note_.event, JoinType.LEFT);
            return criteriaBuilder.and(
                    permissionPredicate,
                    eventPredicateGenerator.viewableEvents(eventJoin, permissionFilter.getAllEmployeeIds(), criteriaBuilder, criteriaQuery)
            );
        }
    }

    public <N extends Note> Predicate hasAccessIgnoringNotViewable(PermissionFilter permissionFilter, From<?, N> noteFrom, From<?, Client> clientJoin,
                                                                   AbstractQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

        var permissionPredicate = permissionPredicateOnlyByClient(permissionFilter, clientJoin, criteriaQuery, criteriaBuilder);

        var eventJoin = noteFrom.join(Note_.event, JoinType.LEFT);
        return criteriaBuilder.and(
                permissionPredicate,
                eventPredicateGenerator.viewableEvents(eventJoin, permissionFilter.getAllEmployeeIds(), criteriaBuilder, criteriaQuery)
        );
    }

    public Predicate permissionPredicateOnlyByClient(PermissionFilter permissionFilter, From<?, Client> clientJoin,
                                                     AbstractQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        var eligible = securityPredicateGenerator.clientInEligibleForDiscoveryCommunity(clientJoin, criteriaBuilder);

        var predicates = new ArrayList<Predicate>();

        if (permissionFilter.hasPermission(Permission.NOTE_VIEW_MERGED_ALL_EXCEPT_OPTED_OUT)) {
            predicates.add(clientPredicateGenerator.isOptedIn(clientJoin, criteriaBuilder));
        }

        if (permissionFilter.hasPermission(Permission.NOTE_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION)) {
            predicates.add(securityPredicateGenerator.associatedOrganizationWithMergedPredicate(criteriaBuilder, clientJoin,
                    criteriaQuery, permissionFilter.getEmployees(Permission.NOTE_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION)));
        }

        if (permissionFilter.hasPermission(Permission.NOTE_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY)) {
            predicates.add(securityPredicateGenerator.associatedCommunityWithMergedClients(criteriaBuilder, clientJoin, criteriaQuery,
                    permissionFilter.getEmployees(Permission.NOTE_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY)));
        }

        if (permissionFilter.hasPermission(Permission.NOTE_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION)) {
            predicates.add(
                    criteriaBuilder.and(
                            clientPredicateGenerator.isOptedIn(clientJoin, criteriaBuilder),
                            securityPredicateGenerator.primaryCommunitiesOfOrganizationsWithMergedClient(
                                    criteriaBuilder,
                                    clientJoin,
                                    criteriaQuery,
                                    permissionFilter.getEmployees(Permission.NOTE_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION)
                            )
                    )
            );
        }

        if (permissionFilter.hasPermission(Permission.NOTE_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY)) {
            predicates.add(
                    criteriaBuilder.and(
                            clientPredicateGenerator.isOptedIn(clientJoin, criteriaBuilder),
                            securityPredicateGenerator.primaryCommunitiesWithMergedClient(
                                    criteriaBuilder,
                                    clientJoin,
                                    criteriaQuery,
                                    permissionFilter.getEmployees(Permission.NOTE_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY)
                            )
                    )
            );
        }

        if (permissionFilter.hasPermission(Permission.NOTE_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM)) {
            predicates.add(securityPredicateGenerator.communityCareTeamWithMergedPredicate(
                    criteriaBuilder,
                    clientJoin,
                    criteriaQuery,
                    permissionFilter.getEmployees(Permission.NOTE_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.current(clientJoin)
            ));
        }

        if (permissionFilter.hasPermission(Permission.NOTE_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM)) {
            predicates.add(securityPredicateGenerator.clientCareTeamWithMergedPredicate(
                    criteriaBuilder,
                    clientJoin,
                    criteriaQuery,
                    permissionFilter.getEmployees(Permission.NOTE_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.current(clientJoin)
            ));
        }

        if (permissionFilter.hasPermission(Permission.NOTE_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)) {
            predicates.add(
                    criteriaBuilder.and(
                            clientPredicateGenerator.isOptedIn(clientJoin, criteriaBuilder),
                            securityPredicateGenerator.addedByEmployeesWithMergedPredicate(
                                    criteriaBuilder,
                                    clientJoin,
                                    criteriaQuery,
                                    permissionFilter.getEmployees(Permission.NOTE_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)
                            )
                    )
            );
        }

        if (permissionFilter.hasPermission(Permission.NOTE_VIEW_MERGED_IF_SELF_CLIENT_RECORD)) {
            predicates.add(securityPredicateGenerator.selfRecordWithMergedPredicate(criteriaBuilder, clientJoin, criteriaQuery,
                    permissionFilter.getEmployees(Permission.NOTE_VIEW_MERGED_IF_SELF_CLIENT_RECORD)));
        }

        if (permissionFilter.hasPermission(Permission.NOTE_VIEW_MERGED_IF_CLIENT_FOUND_IN_RECORD_SEARCH) &&
                CollectionUtils.isNotEmpty(permissionFilter.getClientRecordSearchFoundIds())) {
            predicates.add(clientJoin.get(Client_.id).in(permissionFilter.getClientRecordSearchFoundIds()));
        }

        return criteriaBuilder.and(
                eligible,
                criteriaBuilder.or(predicates.toArray(new Predicate[0]))
        );
    }
}
