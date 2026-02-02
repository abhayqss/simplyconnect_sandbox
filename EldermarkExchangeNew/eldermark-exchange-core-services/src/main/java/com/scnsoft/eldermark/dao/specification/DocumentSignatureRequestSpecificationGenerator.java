package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.Employee_;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember_;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestStatus;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class DocumentSignatureRequestSpecificationGenerator {

    public Specification<DocumentSignatureRequest> byClientOrganizationId(Long organizationId) {
        return (root, query, cb) -> {
            var organizationIdColumn =
                    JpaUtils.getOrCreateJoin(root, DocumentSignatureRequest_.client).get(Client_.organizationId);
            return cb.equal(cb.literal(organizationId), organizationIdColumn);
        };
    }

    public Specification<DocumentSignatureRequest> byClientCommunityId(Long communityId) {
        return (root, query, cb) -> {
            var communityIdColumn =
                    JpaUtils.getOrCreateJoin(root, DocumentSignatureRequest_.client).get(Client_.communityId);
            return cb.equal(cb.literal(communityId), communityIdColumn);
        };
    }

    public Specification<DocumentSignatureRequest> withStatuses(Collection<DocumentSignatureRequestStatus> statuses) {
        return (root, query, cb) -> cb.or(
                root.get(DocumentSignatureRequest_.status).in(statuses)
        );
    }

    public Specification<DocumentSignatureRequest> byBulkRequestId(Long bulkRequestId) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(DocumentSignatureRequest_.bulkRequestId), bulkRequestId);
    }

    public Specification<DocumentSignatureRequest> withDocumentIsNotNull() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNotNull(JpaUtils.getOrCreateJoin(root, DocumentSignatureRequest_.document));
    }

    public Specification<DocumentSignatureRequest> byClientId(Long clientId) {
        return (root, query, cb) -> cb.equal(root.get(DocumentSignatureRequest_.clientId), clientId);
    }

    public Specification<DocumentSignatureRequest> byRecipientCtmOnHold() {
        return (root, query, cb) -> {

            var subQuery = query.subquery(Long.class);
            var subqueryRoot = subQuery.from(ClientCareTeamMember.class);
            var subClient = subqueryRoot.get(ClientCareTeamMember_.clientId);

            var employeeJoin = JpaUtils.getOrCreateJoin(root, DocumentSignatureRequest_.requestedFromEmployee);

            subQuery.select(subqueryRoot.get(ClientCareTeamMember_.employeeId))
                    .where(cb.and(
                                    cb.equal(subClient, root.get(DocumentSignatureRequest_.clientId)),
                                    cb.equal(subqueryRoot.get(ClientCareTeamMember_.employeeId), employeeJoin.get(Employee_.id)),
                                    cb.equal(subqueryRoot.get(ClientCareTeamMember_.onHold), true)
                            )
                    );
            return employeeJoin.get(Employee_.id).in(subQuery);
        };
    }
}
