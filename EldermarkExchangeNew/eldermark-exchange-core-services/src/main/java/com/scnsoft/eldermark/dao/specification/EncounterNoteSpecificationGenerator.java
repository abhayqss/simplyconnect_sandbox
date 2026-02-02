package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.NotePredicateGenerator;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.note.*;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Component
public class EncounterNoteSpecificationGenerator extends AuditableEntitySpecificationGenerator<EncounterNote> {

    @Autowired
    private NotePredicateGenerator notePredicateGenerator;

    public <T extends IdNameAware> Specification<EncounterNote> getByEncounterCodeAndNoteTypesAndCommunityIds(NoteSubType.EncounterCode subTypeCode, List<String> noteTypeCodes, List<T> communities) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Set<Long> communityIds = CareCoordinationUtils.toIdsSet(communities);
            if (isEmpty(communityIds)) {
                return criteriaBuilder.or();
            }

            criteriaQuery.distinct(true); //.join(Note_.noteClients) is many to many

            return criteriaBuilder.and(
                    criteriaBuilder.equal(JpaUtils.getOrCreateJoin(root, Note_.subType).get(NoteSubType_.encounterCode), subTypeCode),
                    criteriaBuilder.in(JpaUtils.getOrCreateJoin(root, EncounterNote_.encounterNoteType).get(EncounterNoteType_.code.getName())).value(noteTypeCodes),
                    JpaUtils.getOrCreateListJoin(root, Note_.noteClients).get(Client_.communityId).in(communityIds)
            );
        };

    }

    public Specification<EncounterNote> hasAccessAndDistinct(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) ->
                notePredicateGenerator.hasAccessAndDistinct(permissionFilter, root, criteriaQuery, criteriaBuilder);
    }

    public Specification<EncounterNote> hasAccessAndDistinctIgnoringNotViewable(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) ->
                notePredicateGenerator.hasAccessAndDistinctIgnoringNotViewable(permissionFilter, root, criteriaQuery, criteriaBuilder);
    }

    public Specification<EncounterNote> betweenDates(Instant dateStart, Instant dateEnd) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(
                        notePredicateGenerator.from(dateStart, criteriaBuilder, root),
                        notePredicateGenerator.to(dateEnd, criteriaBuilder, root)
                );
    }

    public Specification<EncounterNote> leaveLatestFilteredByDates(Instant dateStart, Instant dateEnd) {
        return (root, query, criteriaBuilder) -> {
            Function<Path<EncounterNote>, Predicate> restrictionFunction = encounterNotePath ->  criteriaBuilder.and(
                    notePredicateGenerator.from(dateStart, criteriaBuilder, encounterNotePath),
                    notePredicateGenerator.to(dateEnd, criteriaBuilder, encounterNotePath));
            return auditableEntityPredicateGenerator.leaveLatestWithRestriction(getEntityClass(), dateEnd, root, query, criteriaBuilder, restrictionFunction);
        };
    }

    public Specification<EncounterNote> withinPeriod(Instant fromDate, Instant toDate) {
        return (root, query, criteriaBuilder) -> {
            var date = root.get(EncounterNote_.encounterDate);
            return criteriaBuilder.and(
                criteriaBuilder.greaterThanOrEqualTo(date, fromDate),
                criteriaBuilder.lessThanOrEqualTo(date, toDate)
            );
        };
    }

    public <T extends IdAware> Specification<EncounterNote> byClientCommunityIn(final List<T> accessibleCommunities) {
        return (root, query, criteriaBuilder) -> {
            var communityIds = CareCoordinationUtils.toIdsSet(accessibleCommunities);

            var subQuery = query.subquery(EncounterNote.class);
            var subNote = subQuery.from(EncounterNote.class);

            var clientsJoin = subNote.join(Note_.noteClients);

            return root.in(subQuery.select(subNote)
                .where(criteriaBuilder.in(clientsJoin.get(Client_.COMMUNITY_ID)).value(communityIds)));
        };
    }

    public Specification<EncounterNote> byClientIdIn(Collection<Long> clientIds) {
        return (root, query, criteriaBuilder) -> {

            var subQuery = query.subquery(EncounterNote.class);
            var subNote = subQuery.from(EncounterNote.class);

            var clientsJoin = subNote.join(Note_.noteClients);

            return root.in(subQuery.select(subNote)
                    .where(criteriaBuilder.in(clientsJoin.get(Client_.ID)).value(clientIds)));
        };
    }

    @Override
    protected Class<EncounterNote> getEntityClass() {
        return EncounterNote.class;
    }
}
