package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.SecurityPredicateGenerator;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Employee_;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.entity.video.VideoCallHistory;
import com.scnsoft.eldermark.entity.video.VideoCallHistory_;
import com.scnsoft.eldermark.entity.video.VideoCallParticipantHistory;
import com.scnsoft.eldermark.entity.video.VideoCallParticipantHistory_;
import com.scnsoft.eldermark.service.twilio.ConversationUtils;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Component
public class VideoCallHistorySpecificationGenerator {

    @Autowired
    private SecurityPredicateGenerator securityPredicateGenerator;

    public Specification<VideoCallHistory> hasAccess(PermissionFilter permissionFilter) {

        if (permissionFilter.hasPermission(Permission.ROLE_SUPER_ADMINISTRATOR)) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.and();
        }

        var specs = new ArrayList<EmployeeSubQuerySpec>();

        if (permissionFilter.hasPermission(VIDEO_CALL_VIEW_ASSOCIATED_CLIENT_HISTORY_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(VIDEO_CALL_VIEW_ASSOCIATED_CLIENT_HISTORY_IF_ASSOCIATED_ORGANIZATION);
            var employeeOrgIds = CareCoordinationUtils.getOrganizationIdsSet(employees);
            specs.add((root, q, cb) -> root.get(Client_.organizationId).in(employeeOrgIds));
        }

        if (permissionFilter.hasPermission(VIDEO_CALL_VIEW_ASSOCIATED_CLIENT_HISTORY_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(
                VIDEO_CALL_VIEW_ASSOCIATED_CLIENT_HISTORY_IF_FROM_AFFILIATED_ORGANIZATION
            );
            specs.add((root, q, cb) -> securityPredicateGenerator.primaryCommunitiesOfOrganizations(
                cb,
                q,
                JpaUtils.getOrCreateListJoin(root, Employee_.associatedClients, JoinType.LEFT).get(Client_.communityId),
                employees
            ));
        }

        if (permissionFilter.hasPermission(VIDEO_CALL_VIEW_ASSOCIATED_CLIENT_HISTORY_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(VIDEO_CALL_VIEW_ASSOCIATED_CLIENT_HISTORY_IF_ASSOCIATED_COMMUNITY);
            var employeeCommunitiesIds = CareCoordinationUtils.getCommunityIdsSet(employees);
            specs.add((root, q, cb) -> JpaUtils.getOrCreateListJoin(root, Employee_.associatedClients, JoinType.LEFT)
                .get(Client_.communityId)
                .in(employeeCommunitiesIds));
        }

        if (permissionFilter.hasPermission(VIDEO_CALL_VIEW_ASSOCIATED_CLIENT_HISTORY_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(VIDEO_CALL_VIEW_ASSOCIATED_CLIENT_HISTORY_IF_FROM_AFFILIATED_COMMUNITY);
            specs.add((root, q, cb) -> securityPredicateGenerator.primaryCommunities(
                cb,
                q,
                JpaUtils.getOrCreateListJoin(root, Employee_.associatedClients, JoinType.LEFT).get(Client_.communityId),
                employees
            ));
        }

        if (permissionFilter.hasPermission(VIDEO_CALL_VIEW_OWN_HISTORY)) {
            var employees = permissionFilter.getEmployees(VIDEO_CALL_VIEW_OWN_HISTORY);
            var employeeIds = CareCoordinationUtils.toIdsSet(employees);
            specs.add((root, q, cb) -> root.get(Employee_.id).in(employeeIds));
        }

        if (!specs.isEmpty()) {
            return (root, query, criteriaBuilder) -> {
                var subQuery = query.subquery(String.class);
                var subRoot = subQuery.from(Employee.class);

                var employeeId = subRoot.get(Employee_.id);

                subQuery.select(ConversationUtils.employeeIdToIdentity(employeeId, criteriaBuilder));

                var predicates = specs.stream()
                    .map(s -> s.toPredicate(subRoot, subQuery, criteriaBuilder))
                    .toArray(Predicate[]::new);

                subQuery.where(criteriaBuilder.or(predicates));

                return withIdentitiesPredicate(root, query, subQuery);
            };
        }

        return (root, query, criteriaBuilder) -> criteriaBuilder.or();
    }

    public Specification<VideoCallHistory> withIdentity(String identity) {
        return (root, q, cb) -> withIdentitiesPredicate(root, q, cb.literal(identity));
    }

    public Specification<VideoCallHistory> withoutEndDate() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.isNull(root.get(VideoCallHistory_.endDatetime));
    }

    public Specification<VideoCallHistory> byUpdatedOrInitialConversationSid(String conversationSid) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.equal(root.get(VideoCallHistory_.updatedConversationSid), conversationSid),
                criteriaBuilder.and(
                        criteriaBuilder.isNull(root.get(VideoCallHistory_.updatedConversationSid)),
                        criteriaBuilder.equal(root.get(VideoCallHistory_.initialConversationSid), conversationSid)
                )
        );
    }

    public Specification<VideoCallParticipantHistory> participantsOfCall(Long callHistoryId) {
        return (root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.equal(root.get(VideoCallParticipantHistory_.videoCallHistoryId), callHistoryId);
    }

    private Predicate withIdentitiesPredicate(
        From<?, VideoCallHistory> root,
        AbstractQuery<?> query,
        Expression<String> identity
    ) {
        var subQuery = query.subquery(Long.class);
        var subRoot = subQuery.from(VideoCallParticipantHistory.class);

        subQuery.select(subRoot.get(VideoCallParticipantHistory_.videoCallHistoryId));
        subQuery.where(subRoot.get(VideoCallParticipantHistory_.twilioIdentity).in(identity));

        return root.get(VideoCallHistory_.id).in(subQuery);
    }

    private interface EmployeeSubQuerySpec {
        Predicate toPredicate(Root<Employee> root, AbstractQuery<?> query, CriteriaBuilder criteriaBuilder);
    }
}
