package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.ClientPredicateGenerator;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.event.Event_;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureHistory;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureHistoryAction;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureHistory_;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest_;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class DocumentSignatureHistorySpecificationGenerator {

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    public Specification<DocumentSignatureHistory> byDocumentId(Long documentId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(DocumentSignatureHistory_.documentId), documentId);
    }

    public Specification<DocumentSignatureHistory> bySignatureRequestCompletedEventId(Long eventId) {
        return (root, query, criteriaBuilder) -> {
            var request = JpaUtils.getOrCreateJoin(root, DocumentSignatureHistory_.request);
            var event = JpaUtils.getOrCreateJoin(request, DocumentSignatureRequest_.signedEvent);

            return criteriaBuilder.and(
                    criteriaBuilder.equal(event.get(Event_.id), eventId),
                    root.get(DocumentSignatureHistory_.action)
                            .in(DocumentSignatureHistoryAction.signatureRequestCompletedActions())
            );
        };
    }

    public Specification<DocumentSignatureHistory> byAction(DocumentSignatureHistoryAction action) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(DocumentSignatureHistory_.action), action);
    }

    public Specification<DocumentSignatureHistory> withinPeriod(Instant fromDate, Instant toDate) {
        return (root, query, criteriaBuilder) -> {
            var date = root.get(DocumentSignatureHistory_.date);
            return criteriaBuilder.and(
                    criteriaBuilder.greaterThanOrEqualTo(date, fromDate),
                    criteriaBuilder.lessThanOrEqualTo(date, toDate)
            );
        };
    }

    public <T extends IdAware> Specification<DocumentSignatureHistory> byClientCommunityIn(List<T> communities) {

        return (root, query, criteriaBuilder) -> {
            var communityIds = CareCoordinationUtils.toIdsSet(communities);
            var request = JpaUtils.getOrCreateJoin(root, DocumentSignatureHistory_.request);
            var client = JpaUtils.getOrCreateJoin(request, DocumentSignatureRequest_.client);

            return criteriaBuilder.in(client.get(Client_.COMMUNITY_ID)).value(communityIds);
        };
    }

    public Specification<DocumentSignatureHistory> hasAccess(PermissionFilter permissionFilter) {
        return (root, query, criteriaBuilder) -> {
            var request = JpaUtils.getOrCreateJoin(root, DocumentSignatureHistory_.request);
            var client = JpaUtils.getOrCreateJoin(request, DocumentSignatureRequest_.client);
            return clientPredicateGenerator.hasDetailsAccess(
                    permissionFilter,
                    client,
                    query,
                    criteriaBuilder
            );
        };
    }
}
