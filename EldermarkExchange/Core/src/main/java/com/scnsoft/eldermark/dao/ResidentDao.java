package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.shared.ResidentFilter;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface ResidentDao extends BaseDao<Resident> {
    List<Resident> getResidents(ResidentFilter filter);

    List<Resident> getResidents(ResidentFilter filter, int start, int limit);

    List<Resident> getResidents(ResidentFilter filter, Pageable pageable);

    Long getResidentCount(ResidentFilter filter);

    Resident getResident(long residentId);
    
    Resident getResident(long residentId, Boolean includeOptOut); 

    List<Resident> getResidents(Collection<Long> residentIds);

    Resident getResident(long databaseId, String residentLegacyId);

    Resident getResident(long databaseId, String residentLegacyId, boolean includeOptOut);

    Long getResidentId(String databaseAlternativeId, String residentLegacyId);

    List<Resident> getResidentsByOrganization(long organizationId);

    List<Resident> filterResidentsByOrganization(Collection<Long> residentIds, long organizationId);

    Resident getResidentByIdentityFields(Long organizationId, String ssn, Date dateOfBirth, String lastName, String firstName);

    Resident getResidentInCommunity(long communityId, String residentLegacyId);
    
	Resident getResidentByIdentityFields(Long organizationId, long communityId, String residentLegacyId);

	Date getResidentArchiveDate(Long residentId,Long organizationId);

}
