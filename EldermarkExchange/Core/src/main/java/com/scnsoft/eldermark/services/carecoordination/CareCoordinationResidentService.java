package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.entity.AdmittanceHistory;
import com.scnsoft.eldermark.entity.CareCoordinationResident;
import com.scnsoft.eldermark.schema.Patient;
import com.scnsoft.eldermark.shared.carecoordination.CareTeamRoleDto;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.PatientDto;
import com.scnsoft.eldermark.shared.carecoordination.patients.PatientListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.patients.PatientsFilterDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Created by pzhurba on 23-Oct-15.
 */
@Transactional
public interface CareCoordinationResidentService {

    List<KeyValueDto> getResidentsNamesForEmployee(final Set<Long> employeeId);
    Page<PatientListItemDto> getPatientListItemDtoForEmployee(final Set<Long> employeeIds, PatientsFilterDto filter, Pageable pageable);

    List<CareCoordinationResident> getOrCreateResident(Long communityId, Patient patient);
    CareCoordinationResident createOrUpdateResident(Long communityId, Long id, PatientDto patient);
    boolean isResidentEditable(CareCoordinationResident residentId);
    boolean isResidentEditable(Set<GrantedAuthority> authorities, Long employeeCommunityId, CareCoordinationResident resident);

    CareCoordinationResident get(Long id);

    Boolean toggleResidentActivation(Long communityId, Long patientId);

    void addToIndex();

    List<PatientListItemDto> getMergedResidents(long patientId, Boolean showDeactivated);

    List<Long> getMergedResidentIds(List<Long> patientIds);

    boolean isExistResident(PatientDto patient);

    Long getCommunityId(Long patientId);

    Long getCreatedById(Long patientId);

    List<Long> getResidentsIdsForEmployee(Long employeeId);

    Set<Long> getLoggedEmployeeIdsAvailableForPatient(Long patientId);

    //TODO move to appropriate service
    Pair<Boolean,Set<Long>> getCommunityAdminEmployeeIds(Set<Long> employeeIds);

    void checkAddEditCareTeamAccessToPatientOrThrow(Long careTeamMemberId, Long patientId);

    Set<CareTeamRoleDto> getAllowedCareTeamRoles(Long careTeamMemberId, Long patientId);

    Long getResidentsCountForCurrentUserAndOrganization();

    List<AdmittanceHistory> getAdmittanceHistoryForPatientInCommunity(Long patientId, Long communityId);
}
