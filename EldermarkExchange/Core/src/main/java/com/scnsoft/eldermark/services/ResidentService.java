package com.scnsoft.eldermark.services;

import com.google.common.base.Optional;
import com.scnsoft.eldermark.entity.AdmitIntakeResidentDate;
import com.scnsoft.eldermark.entity.CareCoordinationResident;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.shared.ResidentFilter;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Transactional
public interface ResidentService {
    List<Resident> getResidents(ResidentFilter filter);
    List<Resident> getResidents(ResidentFilter filter, Pageable pageable);

    /**
     * Load a full graph of merged residents
     * @deprecated Use {@link ResidentService#getDirectMergedResidents}
     */
    List<Resident> getMergedResidents(long residentId);
    /**
     * Load residents that are directly merged with the provided resident + residents into which this resident is merged
     */
    Set<Resident> getDirectMergedResidents(Resident resident);
    /**
     * Get resident IDs that are directly merged with the provided resident + residents into which this resident is merged
     */
    Set<Long> getDirectMergedResidentIds(Resident resident);
    /**
     * Load a full graph of "maybe matched" residents
     * @deprecated Use {@link ResidentService#getDirectProbablyMatchedResidents}
     */
    List<Resident> getProbablyMatchedResidents(long residentId);
    /**
     * Load residents that are directly "maybe matched" with the provided resident
     */
    Set<Resident> getDirectProbablyMatchedResidents(Resident resident);

    List<Resident> filterResidentsByOrganization(Collection<Long> residentIds, long organizationId);

    Date getResidentArchiveDate(Long residentId,Long organizationId);
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void matchAndMergeResidents(List<Long> residentIds);
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void unmatchResidents(List<Long> residentIds, List<Long> mismatchedResidentIds);

    Long getResidentCount(ResidentFilter filter);
    Resident getResident(long residentId);
    Resident getResident(long residentId, boolean includeOptOut);
    List<Resident> getResidents(Collection<Long> residentIds);
    Resident getResident(String legacyId, String databaseAlternativeId);
    List<Resident> getResidentsByOrganization(long organizationId);
    Resident getResidentByIdentityFields(Long organizationId, String ssn, Date dateOfBirth, String lastName, String firstName);

    Resident createResident(Resident resident);
    Resident updateResident(Resident resident);
    Resident updateLegacyIds(Resident resident);

    /**
     * Delete resident.
     * It's used only by {@code CcdHL7ParsingServiceIT} and {@code ConsolCcdParsingServiceIT} tests for tear down. Not verified in the main application.
     */
    void deleteResident(Resident resident);

    List<AdmitIntakeResidentDate> getAdmitIntakeHistoryFiltered(Long residentId);

    /**
     * convert resident from similar object with the same function
     * they are mapped to the same DB table
     * @param careCoordinationResident
     * @return
     */
    Resident convert(CareCoordinationResident careCoordinationResident);
    
    Optional<Resident> getResidentByIdentityFields(Long organizationId, Long communityId, String residentLegacyId);

    void updateResidentAccordingToComprehensiveAssessment(Long assessmentId, Long residentAssessmentResultId);
    
}