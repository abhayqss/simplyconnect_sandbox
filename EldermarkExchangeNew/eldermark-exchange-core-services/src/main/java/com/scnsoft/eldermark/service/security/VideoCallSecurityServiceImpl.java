package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.projection.CareTeamRoleCodeAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.twilio.user.EmployeeTwilioSecurityFieldsAware;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.security.CareTeamRolePermissionMapping;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.service.twilio.ChatService;
import com.scnsoft.eldermark.service.twilio.ConversationUtils;
import com.scnsoft.eldermark.service.twilio.VideoCallService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("videoCallSecurityService")
@Transactional(readOnly = true)
public class VideoCallSecurityServiceImpl extends BaseChatVideoCallSecurityService implements VideoCallSecurityService {

    private static final List<Permission> VIDEO_CALL_HISTORY_PERMISSIONS = List.of(
            ROLE_SUPER_ADMINISTRATOR,
            VIDEO_CALL_VIEW_ASSOCIATED_CLIENT_HISTORY_IF_ASSOCIATED_ORGANIZATION,
            VIDEO_CALL_VIEW_ASSOCIATED_CLIENT_HISTORY_IF_FROM_AFFILIATED_ORGANIZATION,
            VIDEO_CALL_VIEW_ASSOCIATED_CLIENT_HISTORY_IF_ASSOCIATED_COMMUNITY,
            VIDEO_CALL_VIEW_ASSOCIATED_CLIENT_HISTORY_IF_FROM_AFFILIATED_COMMUNITY,
            VIDEO_CALL_VIEW_OWN_HISTORY
    );

    public static final List<Permission> VIDEO_CALL_ADD_CONTACT_PERMISSIONS = List.of(
            VIDEO_CALL_ADD_CONTACT_ALL_EXCEPT_OPT_OUT_CLIENT_CONTACT,
            VIDEO_CALL_ADD_CONTACT_IF_ASSOCIATED_ORGANIZATION_EXCEPT_OPT_OUT_CLIENT_CONTACT,
            VIDEO_CALL_ADD_CONTACT_IF_FROM_PRIMARY_ORGANIZATION_EXCEPT_OPT_OUT_CLIENT_CONTACT,
            VIDEO_CALL_ADD_CONTACT_IF_FROM_AFFILIATED_ORGANIZATION_EXCEPT_OPT_OUT_CLIENT_CONTACT,
            VIDEO_CALL_ADD_CONTACT_IF_FROM_PRIMARY_COMMUNITY_EXCEPT_OPT_OUT_CLIENT_CONTACT,
            VIDEO_CALL_ADD_CONTACT_IF_FROM_AFFILIATED_COMMUNITY_EXCEPT_OPT_OUT_CLIENT_CONTACT,
            VIDEO_CALL_ADD_CONTACT_IF_CREATED_BY_SELF_EXCEPT_OPT_OUT_CLIENT_CONTACT,
            VIDEO_CALL_ADD_CONTACT_IF_SHARE_CURRENT_RP_CTM_EXCEPT_OPT_OUT_CLIENT_CONTACT,
            VIDEO_CALL_ADD_CONTACT_IF_SELF_RECORD_CURRENT_RP_CLIENT_CTM_EXCEPT_OPT_OUT_CLIENT_CONTACT,
            VIDEO_CALL_ADD_CONTACT_IF_ACCESSIBLE_CLIENT_ASSOCIATED_CONTACT
    );

    @Autowired
    private ChatService chatService;

    @Autowired
    private VideoCallService videoCallService;

    @Autowired
    private IncidentReportSecurityService incidentReportSecurityService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public boolean canStart(String conversationSid, Collection<Long> employeeIds) {
        return canStart(conversationSid, employeeIds, currentUserFilter());
    }

    @Override
    public boolean canStart(String conversationSid, Collection<Long> employeeIds, PermissionFilter filter) {
        if (StringUtils.isNotEmpty(conversationSid)) {
            if (!chatService.isAnyChatParticipant(conversationSid, filter.getAllEmployeeIds())) {
                return false;
            }

            var participantIds = ConversationUtils.employeeIdsFromIdentities(chatService.findActiveUserIdentitiesByConversationSids(
                    List.of(conversationSid),
                    loggedUserService.getCurrentEmployeeId()
            ));

            if (CollectionUtils.isNotEmpty(employeeIds)) {
                // If users are in the same chat, it is allowed to call them. Just check that all of these users have video feature
                if (new HashSet<>(participantIds).containsAll(employeeIds)) {
                    return employeeService.findAllById(employeeIds, EmployeeSecurityFieldsAware.class).stream()
                            .filter(e -> e.getStatus() == EmployeeStatus.ACTIVE) //only active users are called
                            .allMatch(this::areVideoCallsAccessibleByEmployee);
                } else {
                    return false;
                }
            } else {
                // Check if conversation contains at least one active user with enabled video calls
                return employeeService.findAllById(participantIds, EmployeeSecurityFieldsAware.class).stream()
                        .filter(e -> e.getStatus() == EmployeeStatus.ACTIVE)
                        .anyMatch(this::areVideoCallsAccessibleByEmployee);
            }
        } else {
            return canAddMembers(employeeIds, filter);
        }
    }

    @Override
    public boolean canAddMembers(Collection<Long> employeeIds) {
        return canAddMembers(employeeIds, currentUserFilter());
    }

    private boolean canAddMembers(Collection<Long> employeeIds, PermissionFilter filter) {
        var distinct = new HashSet<>(employeeIds);
        distinct.removeAll(filter.getAllEmployeeIds());
        return !distinct.isEmpty() && distinct.stream().allMatch(employeeId -> videoCallService.isVideoCallEnabled(employeeId) &&
                canAddEmployee(filter, employeeId,
                        VIDEO_CALL_ADD_CONTACT_ALL_EXCEPT_OPT_OUT_CLIENT_CONTACT,
                        VIDEO_CALL_ADD_CONTACT_IF_ASSOCIATED_ORGANIZATION_EXCEPT_OPT_OUT_CLIENT_CONTACT,
                        VIDEO_CALL_ADD_CONTACT_IF_FROM_PRIMARY_ORGANIZATION_EXCEPT_OPT_OUT_CLIENT_CONTACT,
                        VIDEO_CALL_ADD_CONTACT_IF_FROM_AFFILIATED_ORGANIZATION_EXCEPT_OPT_OUT_CLIENT_CONTACT,
                        VIDEO_CALL_ADD_CONTACT_IF_FROM_PRIMARY_COMMUNITY_EXCEPT_OPT_OUT_CLIENT_CONTACT,
                        VIDEO_CALL_ADD_CONTACT_IF_FROM_AFFILIATED_COMMUNITY_EXCEPT_OPT_OUT_CLIENT_CONTACT,
                        VIDEO_CALL_ADD_CONTACT_IF_CREATED_BY_SELF_EXCEPT_OPT_OUT_CLIENT_CONTACT,
                        VIDEO_CALL_ADD_CONTACT_IF_SHARE_CURRENT_RP_CTM_EXCEPT_OPT_OUT_CLIENT_CONTACT,
                        VIDEO_CALL_ADD_CONTACT_IF_SELF_RECORD_CURRENT_RP_CLIENT_CTM_EXCEPT_OPT_OUT_CLIENT_CONTACT,
                        VIDEO_CALL_ADD_CONTACT_IF_ACCESSIBLE_CLIENT_ASSOCIATED_CONTACT));
    }

    @Override
    public boolean canViewHistory() {
        return hasAnyPermission(VIDEO_CALL_HISTORY_PERMISSIONS);
    }

    @Override
    public boolean canStartIrCall(String conversationSid, Long incidentReportId) {
        var filter = currentUserFilter();
        if (StringUtils.isNotEmpty(conversationSid)) {
            return chatService.isAnyChatParticipant(conversationSid, filter.getAllEmployeeIds());
        }
        return incidentReportSecurityService.canView(incidentReportId);
    }

    @Override
    public boolean areVideoCallsAccessibleByEmployee(Employee employee) {
        return areVideoCallsAccessibleByEmployee(EmployeeSecurityFieldsAware.of(employee));
    }

    private boolean areVideoCallsAccessibleByEmployee(EmployeeSecurityFieldsAware employee) {
        var rolesWithEnabledVideoCalls =
                CareTeamRolePermissionMapping.findCareTeamRoleCodesWithAnyPermission(VIDEO_CALL_ADD_CONTACT_PERMISSIONS);
        return videoCallService.isVideoCallEnabled(employee)
                && (employee.getCareTeamRoleCode() != null && rolesWithEnabledVideoCalls.contains(employee.getCareTeamRoleCode()));
    }

    private interface EmployeeSecurityFieldsAware extends EmployeeTwilioSecurityFieldsAware, CareTeamRoleCodeAware {
        static EmployeeSecurityFieldsAware of(Employee e) {
            return new EmployeeSecurityFieldsAware() {
                @Override
                public CareTeamRoleCode getCareTeamRoleCode() {
                    var careTeamRole = e.getCareTeamRole();
                    return careTeamRole != null ? careTeamRole.getCode() : null;
                }

                @Override
                public Boolean getOrganizationIsChatEnabled() {
                    return e.getOrganization().isChatEnabled();
                }

                @Override
                public Boolean getOrganizationIsVideoEnabled() {
                    return e.getOrganization().isVideoEnabled();
                }

                @Override
                public EmployeeStatus getStatus() {
                    return e.getStatus();
                }
            };
        }
    }
}
