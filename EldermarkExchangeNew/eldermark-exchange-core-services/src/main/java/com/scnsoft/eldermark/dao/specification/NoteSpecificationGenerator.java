package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.predicate.ClientPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.NotePredicateGenerator;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.note.Note;
import com.scnsoft.eldermark.entity.note.Note_;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class NoteSpecificationGenerator extends AuditableEntitySpecificationGenerator<Note> {

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    @Autowired
    private NotePredicateGenerator notePredicateGenerator;

    //TODO split into 2 methods (commented below) and resolve ListJoin to clients
    public Specification<Note> byClientIdAndMergedAndHasAccessAndDistinct(PermissionFilter permissionFilter, Long clientId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var clientJoin = root.join(Note_.noteClients);
            criteriaQuery.distinct(true);
            var predicates = new ArrayList<Predicate>();
            if (clientId != null) {
                predicates.add(clientPredicateGenerator.clientAndMergedClients(criteriaBuilder,
                    clientJoin, criteriaQuery, Collections.singletonList(clientId)));
            }
            predicates.add(notePredicateGenerator.hasAccess(permissionFilter, root, clientJoin, criteriaQuery, criteriaBuilder));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    protected Class<Note> getEntityClass() {
        return Note.class;
    }

    public Specification<Note> byClientIdAndMerged(Long clientId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (clientId == null) {
                return criteriaBuilder.or();
            }

            var subQuery = criteriaQuery.subquery(Note.class);
            var subNote = subQuery.from(Note.class);

            var clientIdsJoin = subNote.join(Note_.noteClientIds);

            return root.in(subQuery.select(subNote)
                    .where(clientPredicateGenerator.clientAndMergedClientsById(criteriaBuilder,
                            clientIdsJoin, subQuery, Collections.singletonList(clientId))));
        };
    }

    public Specification<Note> hasAccessAndDistinct(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) ->
                notePredicateGenerator.hasAccessAndDistinct(permissionFilter, root, criteriaQuery, criteriaBuilder);
    }

    public Specification<Note> withinPeriod(Instant fromDate, Instant toDate) {
        return (root, query, criteriaBuilder) -> {
            var date = root.get(Note_.noteDate);
            var lastModifiedDate = root.get(Note_.lastModifiedDate);
            return criteriaBuilder.or(criteriaBuilder.and(
                criteriaBuilder.greaterThanOrEqualTo(lastModifiedDate, fromDate),
                criteriaBuilder.lessThanOrEqualTo(lastModifiedDate, toDate)
                ),
                criteriaBuilder.and(
                    criteriaBuilder.greaterThanOrEqualTo(date, fromDate),
                    criteriaBuilder.lessThanOrEqualTo(date, toDate)
                ));
        };
    }

    public <T extends IdAware> Specification<Note> byClientCommunityIn(List<T> communities) {
        return (root, query, criteriaBuilder) -> {
            var communityIds = CareCoordinationUtils.toIdsSet(communities);

            var subQuery = query.subquery(Note.class);
            var subNote = subQuery.from(Note.class);

            var clientsJoin = subNote.join(Note_.noteClients);

            return root.in(subQuery.select(subNote)
                .where(criteriaBuilder.in(clientsJoin.get(Client_.COMMUNITY_ID)).value(communityIds)));
        };
    }

    public Specification<Note> isArchived(boolean isArchived) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Note_.archived), isArchived);
    }

    public Specification<Note> byClientIdIn(Collection<Long> clientIds) {
        return (root, query, criteriaBuilder) -> root.get(Note_.clientId).in(clientIds);
    }
}
