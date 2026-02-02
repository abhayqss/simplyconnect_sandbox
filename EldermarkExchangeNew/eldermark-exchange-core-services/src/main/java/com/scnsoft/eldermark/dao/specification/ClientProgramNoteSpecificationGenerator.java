package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.NotePredicateGenerator;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.note.ClientProgramNote;
import com.scnsoft.eldermark.entity.note.ClientProgramNote_;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.JoinType;
import java.time.Instant;
import java.util.Collection;

@Component
public class ClientProgramNoteSpecificationGenerator {

    @Autowired
    private NotePredicateGenerator notePredicateGenerator;

    public Specification<ClientProgramNote> hasAccess(PermissionFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var subQuery = criteriaQuery.subquery(Long.class);
            var subRoot = subQuery.from(ClientProgramNote.class);
            subQuery.select(subRoot.get(ClientProgramNote_.id)).where(notePredicateGenerator
                    .hasAccess(filter, subRoot, JpaUtils.getOrCreateListJoin(subRoot, ClientProgramNote_.noteClients), criteriaQuery, criteriaBuilder));

            return criteriaBuilder.or(
                    notePredicateGenerator.hasAccess(filter, root, JpaUtils.getOrCreateJoin(root, ClientProgramNote_.client, JoinType.LEFT), criteriaQuery, criteriaBuilder),
                    root.get(ClientProgramNote_.id).in(subQuery)
            );
        };
    }

    public <T extends IdNameAware> Specification<ClientProgramNote> byCommunities(Collection<T> communities) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var communityIds = CareCoordinationUtils.toIdsSet(communities);
            if (CollectionUtils.isEmpty(communityIds)) {
                return criteriaBuilder.or();
            }
            var subQuery = criteriaQuery.subquery(Long.class);
            var subRoot = subQuery.from(ClientProgramNote.class);
            subQuery.select(subRoot.get(ClientProgramNote_.id)).where(JpaUtils.getOrCreateListJoin(subRoot, ClientProgramNote_.noteClients).get(Client_.communityId).in(communityIds));

            return CollectionUtils.isEmpty(communityIds) ? criteriaBuilder.or() :
                    criteriaBuilder.or(
                            JpaUtils.getOrCreateJoin(root, ClientProgramNote_.client, JoinType.LEFT).get(Client_.communityId).in(communityIds),
                            root.get(ClientProgramNote_.id).in(subQuery)
                    );
        };
    }

    public Specification<ClientProgramNote> inProgress(Instant from, Instant to) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.lessThanOrEqualTo(root.get(ClientProgramNote_.startDate), to),
                criteriaBuilder.greaterThanOrEqualTo(root.get(ClientProgramNote_.endDate), from)
        );
    }
}
