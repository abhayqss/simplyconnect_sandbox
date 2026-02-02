package com.scnsoft.eldermark.dao.predicate;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.entity.Employee_;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember_;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.criteria.*;

public abstract class CareTeamMemberPredicateGenerator<T extends CareTeamMember> {

    @Autowired
    protected OrganizationPredicateGenerator organizationPredicateGenerator;

    @Autowired
    private EmployeePredicateGenerator employeePredicateGenerator;

    public Predicate ofAffiliationType(From<?, T> root, CriteriaBuilder criteriaBuilder, AffiliatedCareTeamType type) {
        switch (type) {
            case REGULAR:
                return ofTheSameOrganization(root, criteriaBuilder);
            case PRIMARY:
                return ofDifferentOrganizations(root, criteriaBuilder);
            case REGULAR_AND_PRIMARY:
                return criteriaBuilder.and();
        }
        throw new RuntimeException("Unknown affiliation type");
    }

    public Predicate ofTheSameOrganization(From<?, T> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(getEntityOrganizationId(root), getEmployeeOrganizationId(root));
    }

    public Predicate ofDifferentOrganizations(From<?, T> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.notEqual(getEntityOrganizationId(root), getEmployeeOrganizationId(root));
    }

    private Path<Long> getEmployeeOrganizationId(From<?, T> careTeamMember) {
        var employee = JpaUtils.getOrCreateJoin(careTeamMember, CareTeamMember_.employee);

        return employee.get(Employee_.organizationId);
    }

    protected abstract Path<Long> getEntityOrganizationId(From<?, T> careTeamMember);

    public abstract Predicate hasAccess(PermissionFilter permissionFilter, From<?, T> root, AbstractQuery<?> query,
                                        CriteriaBuilder criteriaBuilder);

    public Predicate chatAccessible(PermissionFilter permissionFilter, Long excludedEmployeeId,
                                    From<?, T> root, AbstractQuery<?> query, CriteriaBuilder criteriaBuilder) {
        var employeeJoin = JpaUtils.getOrCreateJoin(root, CareTeamMember_.employee);
        var hasChatAccess = employeePredicateGenerator.hasChatAccess(permissionFilter, employeeJoin, query, criteriaBuilder);

        return conversationAccessible(permissionFilter, excludedEmployeeId, root, query, criteriaBuilder, hasChatAccess);
    }

    public Predicate videoCallAccessible(PermissionFilter permissionFilter, Long excludedEmployeeId,
                                         From<?, T> root, AbstractQuery<?> query, CriteriaBuilder criteriaBuilder) {
        var employeeJoin = JpaUtils.getOrCreateJoin(root, CareTeamMember_.employee);
        var hasVideoCallAccess = employeePredicateGenerator.hasVideoCallAccess(permissionFilter, employeeJoin, query, criteriaBuilder);

        return conversationAccessible(permissionFilter, excludedEmployeeId, root, query, criteriaBuilder, hasVideoCallAccess);
    }

    public Predicate conversationAccessible(PermissionFilter permissionFilter, Long excludedEmployeeId,
                                            From<?, T> root, AbstractQuery<?> query, CriteriaBuilder criteriaBuilder,
                                            Predicate hasConversationAccess) {
        var employeeJoin = JpaUtils.getOrCreateJoin(root, CareTeamMember_.employee);

        //although a contact can be a care team member, he can be visible not as
        //care team member to logged in user, and, therefore, he shouldn't appear
        //in the list of care team members
        var hasAccess = hasAccess(permissionFilter, root, query, criteriaBuilder);
        var activeEmployee = employeePredicateGenerator.isActive(employeeJoin, criteriaBuilder);

        var exclude = excludedEmployeeId == null ? criteriaBuilder.and() :
                employeePredicateGenerator.excludeEmployeeId(excludedEmployeeId, employeeJoin, criteriaBuilder);

        var notOnHold = ofConsentType(root,
                criteriaBuilder,
                query,
                HieConsentCareTeamType.current(HieConsentCareTeamType.ANY_TARGET_CLIENT_ID)
        );

        return criteriaBuilder.and(
                hasConversationAccess,
                hasAccess,
                activeEmployee,
                exclude,
                notOnHold
        );
    }

    public abstract Predicate ofConsentType(From<?, T> root,
                                            CriteriaBuilder criteriaBuilder,
                                            AbstractQuery<?> query,
                                            HieConsentCareTeamType consentType);

    public Predicate byEmployeeId(From<?, T> root, CriteriaBuilder criteriaBuilder, Path<Long> employeeId) {
        return criteriaBuilder.equal(root.get(CareTeamMember_.employeeId), employeeId);
    }
}
