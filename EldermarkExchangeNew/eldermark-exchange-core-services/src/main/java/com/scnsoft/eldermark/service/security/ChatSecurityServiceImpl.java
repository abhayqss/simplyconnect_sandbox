package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.security.CareTeamRolePermissionMapping;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.twilio.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("chatSecurityService")
@Transactional(readOnly = true)
public class ChatSecurityServiceImpl extends BaseChatVideoCallSecurityService implements ChatSecurityService {

    public static final Set<Permission> CHAT_PERMISSIONS = Set.of(
            Permission.CHAT_ADD_CONTACT_ALL_EXCEPT_OPT_OUT_CLIENT_CONTACT,
            Permission.CHAT_ADD_CONTACT_IF_ASSOCIATED_ORGANIZATION_EXCEPT_OPT_CLIENT_CONTACT,
            Permission.CHAT_ADD_CONTACT_IF_FROM_PRIMARY_ORGANIZATION_EXCEPT_OPT_CLIENT_CONTACT,
            Permission.CHAT_ADD_CONTACT_IF_FROM_AFFILIATED_ORGANIZATION_EXCEPT_OPT_CLIENT_CONTACT,
            Permission.CHAT_ADD_CONTACT_IF_FROM_PRIMARY_COMMUNITY_EXCEPT_OPT_CLIENT_CONTACT,
            Permission.CHAT_ADD_CONTACT_IF_FROM_AFFILIATED_COMMUNITY_EXCEPT_OPT_CLIENT_CONTACT,
            Permission.CHAT_ADD_CONTACT_IF_CREATED_BY_SELF_EXCEPT_OPT_CLIENT_CONTACT,
            Permission.CHAT_ADD_CONTACT_IF_SHARE_CURRENT_RP_CTM_EXCEPT_OPT_CLIENT_CONTACT,
            Permission.CHAT_ADD_CONTACT_IF_SELF_RECORD_CURRENT_RP_CLIENT_CTM_EXCEPT_OPT_CLIENT_CONTACT,
            Permission.CHAT_ADD_CONTACT_IF_ACCESSIBLE_CLIENT_ASSOCIATED_CONTACT
    );

    @Autowired
    protected ChatService chatService;

    @Override
    public boolean canStart(Collection<Long> employeeIds) {
        var filter = currentUserFilter();
        return canStart(employeeIds, filter);
    }

    @Override
    public boolean canStart(Collection<Long> employeeIds, PermissionFilter filter) {
        var distinct = new HashSet<>(employeeIds);
        distinct.removeAll(filter.getAllEmployeeIds()); //exclude self. todo verify for linked

        return !distinct.isEmpty() && distinct.stream().allMatch(employeeId -> chatService.isChatEnabled(employeeId) &&
                canAddEmployee(
                        filter,
                        employeeId,
                        CHAT_ADD_CONTACT_ALL_EXCEPT_OPT_OUT_CLIENT_CONTACT,
                        CHAT_ADD_CONTACT_IF_ASSOCIATED_ORGANIZATION_EXCEPT_OPT_CLIENT_CONTACT,
                        CHAT_ADD_CONTACT_IF_FROM_PRIMARY_ORGANIZATION_EXCEPT_OPT_CLIENT_CONTACT,
                        CHAT_ADD_CONTACT_IF_FROM_AFFILIATED_ORGANIZATION_EXCEPT_OPT_CLIENT_CONTACT,
                        CHAT_ADD_CONTACT_IF_FROM_PRIMARY_COMMUNITY_EXCEPT_OPT_CLIENT_CONTACT,
                        CHAT_ADD_CONTACT_IF_FROM_AFFILIATED_COMMUNITY_EXCEPT_OPT_CLIENT_CONTACT,
                        CHAT_ADD_CONTACT_IF_CREATED_BY_SELF_EXCEPT_OPT_CLIENT_CONTACT,
                        CHAT_ADD_CONTACT_IF_SHARE_CURRENT_RP_CTM_EXCEPT_OPT_CLIENT_CONTACT,
                        CHAT_ADD_CONTACT_IF_SELF_RECORD_CURRENT_RP_CLIENT_CTM_EXCEPT_OPT_CLIENT_CONTACT,
                        CHAT_ADD_CONTACT_IF_ACCESSIBLE_CLIENT_ASSOCIATED_CONTACT
                ));
    }

    @Override
    public boolean canAddMembers(Collection<Long> employeeIds) {
        var filter = currentUserFilter();
        return canStart(employeeIds, filter);
    }

    @Override
    public boolean existsConversationWithClient(Long clientId) {
        var filter = currentUserFilter();
        return chatService.existsConversationBetweenAnyAndClient(filter.getAllEmployeeIds(), clientId);
    }

    @Override
    public boolean existsConversationWithEmployee(Long employeeId) {
        var filter = currentUserFilter();
        return chatService.existsConversationBetweenAnyAndEmployee(filter.getAllEmployeeIds(), employeeId);
    }

    @Override
    public boolean areChatsAccessibleByEmployee(Employee employee) {
        var rolesWithEnabledChat = CareTeamRolePermissionMapping.findCareTeamRoleCodesWithAnyPermission(CHAT_PERMISSIONS);
        return chatService.isChatEnabled(employee)
                && rolesWithEnabledChat.contains(employee.getCareTeamRole().getCode());
    }
}
