package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.CareTeamFilter;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collection;
import java.util.List;

public interface CareTeamMemberService {

    void deleteById(Long careTeamMemberId, Long performedById);

    Page<CareTeamMember> find(CareTeamFilter filter, PermissionFilter permissionFilter, Pageable pageable);

    List<? extends CareTeamMember> find(CareTeamFilter filter, PermissionFilter permissionFilter);

    CareTeamMember findById(Long careTeamMemberId);

    <T extends CareTeamMember> T save(T careTeamMember, Long performedById);

    List<EventTypeCareTeamRoleXref> getResponsibilitiesForRole(Long careTeamRoleId);

    List<NotificationType> defaultNotificationChannels();

    <T> List<T> getContactsOrganizationsSuitableForCommunityCareTeam(PermissionFilter filter, Long communityId, AffiliatedCareTeamType type,
                                                                     Sort sort, Class<T> projection);

    <T> List<T> getContactsOrganizationsSuitableForClientCareTeam(PermissionFilter filter, Long clientId, AffiliatedCareTeamType type,
                                                                  Sort sort, Class<T> projection);

    List<IdNamesAware> getContactsSuitableForCommunityCareTeam(Long organizationId, Sort sort);

    List<IdNamesAware> getContactsSuitableForClientCareTeam(Long organizationId, Long clientId, Sort sort);

    Long count(CareTeamFilter filter, PermissionFilter permissionFilter);

    boolean exists(CareTeamFilter filter, PermissionFilter permissionFilter);

    List<CareTeamMember> getFullCareTeam(List<Client> client);

    boolean doesAnyShareCtmWithEmployee(Collection<Long> sourceEmployeeIds, Long targetEmployeeId, HieConsentCareTeamType consentType);

    Long getContactsSuitableForClientCareTeamCount(Long organizationId);

    Long getContactsSuitableForCommunityCareTeamCount(Long organizationId);

    <T> List<T> findByClientIdInAndRoleCodeIn(Collection<Long> clientIds, List<CareTeamRoleCode> careTeamRoleCode, Class<T> projectionClass);

    boolean doesEmployeeWithSameLoginExistInClientCareTeam(Long clientId, String login);
}
