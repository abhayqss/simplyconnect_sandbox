package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.entity.CareCoordinationResident;
import com.scnsoft.eldermark.entity.CareTeamMember;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.ResidentCareTeamMember;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.careteam.CareTeamMemberDto;
import com.scnsoft.eldermark.shared.carecoordination.careteam.CareTeamMemberListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.patients.NotificationPreferencesDto;
import com.scnsoft.eldermark.shared.carecoordination.patients.NotificationPreferencesGroupDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * @author mradzivonenka
 * @author Netkachev
 * @author phomal
 * @author pzhurba
 * Created by pzhurba on 20-Oct-15.
 */
@Transactional
public interface CareTeamService {
    @Transactional(readOnly = true)
    Page<CareTeamMemberListItemDto> getPatientCareTeam(Long patientId, Boolean affiliated, final Pageable pageable);

    @Transactional(readOnly = true)
    Page<CareTeamMemberListItemDto> getCommunityCareTeam(final Long organizationId, Boolean affiliated, final Pageable pageable);

    // Not used. Replaced with getCareTeamMembersAvailableToReceiveEventNotificationsForPatient()
    @Transactional(readOnly = true)
    List<CareTeamMember> getCareTeamMembersForPatient(final CareCoordinationResident resident);

    @Transactional(readOnly = true)
    List<CareTeamMember> getCareTeamMembersAvailableToReceiveEventNotificationsForPatient(final CareCoordinationResident resident, final Set<Long> mergedResidentIds);

    @Transactional(readOnly = true)
    List<NotificationPreferencesGroupDto> getAvailableNotificationPreferences(final Long careTeamRoleId, Long careTeamMemberId, Long employeeId);

    void deleteResidentCareTeamMember(long careTeamMemberId);

    void createOrUpdateResidentCareTeamMember(Long residentId, CareTeamMemberDto patientCareTeamMemberDto);

    void createOrUpdateCommunityCareTeamMember(Long communityId, CareTeamMemberDto patientCareTeamMemberDto, boolean canEditSelf, boolean createdAutomatically);

    void deleteCommunityCareTeamMember(long careTeamMemberId);

    Long getEmployeeIdForCareTeamMember(long careTeamMemberId);

    KeyValueDto getEmployeeForCareTeamMember(long careTeamMemberId);

//    Long getEmployeeIdForPatientCareTeamMember(long careTeamMemberId);

//    KeyValueDto getEmployeeForPatientCareTeamMember(long careTeamMemberId);

    CareTeamMember getCareTeamMember(Long id);

    boolean checkHasCareTeamMember(Set<Long> employeeIds, Long residentId, Long organizationId);

    void deleteCareTeamMembersAssociatedWithDeletedAffiliatedRelation();

    Boolean getIncludedInFaceSheetForCareTeamMember(Long careTeamMemberId);
}
