package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.AppointmentContactFilter;
import com.scnsoft.eldermark.beans.ContactFilter;
import com.scnsoft.eldermark.beans.ContactNameFilter;
import com.scnsoft.eldermark.beans.conversation.AccessibleChatContactFilter;
import com.scnsoft.eldermark.beans.conversation.ConversationParticipatingAccessibilityFilter;
import com.scnsoft.eldermark.beans.conversation.EmployeeSearchWithFavouriteFilter;
import com.scnsoft.eldermark.beans.projection.IdNamesCareTeamRoleNameAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dto.employee.EmployeeUpdates;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeBasic;
import com.scnsoft.eldermark.entity.IdNamesAware;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.entity.projection.EmployeeRoleNameStatusCommunityAware;
import com.scnsoft.eldermark.projection.EmployeeIdNameFavouriteOrgDetails;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContactService extends ProjectingService<Long> {

    Page<EmployeeBasic> find(ContactFilter filter, PermissionFilter permissionFilter, Pageable pageRequest);

    Employee findById(Long contactId);

    Employee save(Employee contact, Long performedById);

    Employee update(EmployeeUpdates employeeUpdates, Long performedById);

    Long count(ContactFilter filter, PermissionFilter permissionFilter);

    Boolean existsByLoginInOrganization(String login, Long organizationId);

    List<IdNamesAware> findNames(ContactNameFilter filter);

    void updateTwilioSid(Long id, String twilioUserSid);

    boolean existsChatAccessible(PermissionFilter permissionFilter, ConversationParticipatingAccessibilityFilter filter);

    List<IdNamesAware> findChatAccessibleNames(PermissionFilter permissionFilter, AccessibleChatContactFilter filter);

    void setTwilioServiceConversation(Long id, String twilioServiceConversationSid);

    Page<EmployeeIdNameFavouriteOrgDetails> findChatAccessible(PermissionFilter permissionFilter,
                                                               EmployeeSearchWithFavouriteFilter filter, Pageable pageRequest);

    boolean existsChatAccessible(PermissionFilter permissionFilter,
                                 EmployeeSearchWithFavouriteFilter filter);

    void setFavourite(Long id, boolean favourite, Long addedById);

    List<IdNamesCareTeamRoleNameAware> findNamesWithRoles(ContactNameFilter filter);

    List<EmployeeRoleNameStatusCommunityAware> findAppointmentContacts(AppointmentContactFilter filter);

    Pair<Double, Double> findAddressCoordinatesById(Long contactId);

    List<CareTeamRole> getQaUnavailableRoles();
}
