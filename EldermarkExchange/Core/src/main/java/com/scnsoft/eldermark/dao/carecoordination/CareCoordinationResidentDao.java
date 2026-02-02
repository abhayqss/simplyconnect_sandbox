package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.dao.BaseDao;
import com.scnsoft.eldermark.entity.CareCoordinationResident;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.services.carecoordination.CareCoordinationResidentFilter;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.PatientDto;
import com.scnsoft.eldermark.shared.carecoordination.patients.PatientListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.patients.PatientsFilterDto;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by pzhurba on 23-Oct-15.
 */
public interface CareCoordinationResidentDao extends BaseDao<CareCoordinationResident> {

    List<CareCoordinationResident> getResidentsForEmployeeByResidentCareTeam(final Long employeeId);

    List<PatientListItemDto> getResidentsForEmployee(final Set<Long> employeeIds, final PatientsFilterDto filter, final Long databaseId, final List<Long> communityIds, final Pageable pageable, boolean isAdmin, Set<Long> employeeCommunityIds);

    List<Long> getResidentIdsForEmployee(final Set<Long> employeeIds, final Long databaseId, final List<Long> filterCommunityIds, boolean isAdmin, Set<Long> employeeCommunityIds);

    List<PatientListItemDto> getMergedResidents(final List<Long> residentIds, Long databaseId, Boolean showDeactivated);

    List<Long> getMergedResidentIds(final List<Long> residentIds, Long databaseId);

    List<KeyValueDto> getResidentNamesForEmployee(final Set<Long> employeeId, final Long databaseId, final List<Long> communityIds, boolean isAdmin, Set<Long> employeeCommunityIds);


    Long getResidentsForEmployeeCount(final Set<Long> employeeIds, PatientsFilterDto filter, final Long databaseId, final List<Long> communityIds, boolean isAdmin, Set<Long> employeeCommunityIds);

    List<CareCoordinationResident> findCareCoordinationResident(CareCoordinationResidentFilter filter, Organization organization);

    CareCoordinationResident getResidentByIdentityFields(Long organizationId, String ssn, Date dateOfBirth, String lastName, String firstName);

    boolean checkExistResidentByIdentityFields(Long organizationId, String ssn, Date dateOfBirth, String lastName, String firstName);

    /**
     * Shortcut for {@link CareCoordinationResidentDao#getResidentsByData(String, String, String, String, String)}
     */
    List<CareCoordinationResident> getResidentsByData(String ssn, String email, String phone);
    List<CareCoordinationResident> getResidentsByData(String ssn, String email, String phone, String firstName, String lastName);

    void createIndex();

    void deleteIndex(Long residentId);

    boolean isFirstTimeIndexed();

    List <CareCoordinationResident>search(PatientDto patient);

    List<CareCoordinationResident>  getLastUpdatedResidents();

    void addToIndex(CareCoordinationResident resident);

    void updateMpiLog();

    List<String> getDeletedResidentRecords();

    List<Long> getResidentIdsCreatedByEmployeeId(Set<Long> employeeIds, Long databaseId);

    Long getCommunityId(Long residentId);

    Long getCreatedById(Long residentId);

    void deletePersonAddresses(Long personId);

    Long getResidentIdWithMemberId(String memberId, Long databaseId, Long communityId);

    Long getResidentIdWithMedicaidNumber(String medicaidNumber, Long databaseId, Long communityId);

    Long getResidentIdWithMedicareNumber(String medicareNumber, Long databaseId, Long communityId);

}
