package com.scnsoft.eldermark.service.palatiumcare;

import com.scnsoft.eldermark.dao.carecoordination.CareTeamMemberDao;
import com.scnsoft.eldermark.dao.palatiumcare.FacilityDao;
import com.scnsoft.eldermark.entity.CareTeamMember;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.mapper.palatiumcare.NotifyFacilityMapper;
import com.scnsoft.eldermark.shared.palatiumcare.FacilityDto;
import com.scnsoft.eldermark.entity.palatiumcare.Facility;
import com.scnsoft.eldermark.shared.palatiumcare.GenericMapper;
import com.scnsoft.eldermark.services.palatiumcare.BasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacilityService extends BasicService<Facility, FacilityDto> {

    private FacilityDao facilityDao;

    private CareTeamMemberDao careTeamMemberDao;

    @Autowired
    public void setFacilityDao(FacilityDao facilityDao) {
        this.facilityDao = facilityDao;
    }

    @Autowired
    public void setCareTeamMemberDao(CareTeamMemberDao careTeamMemberDao) {
        this.careTeamMemberDao = careTeamMemberDao;
    }

    @Override
    protected GenericMapper<Facility, FacilityDto> getMapper() {
        return new NotifyFacilityMapper();
    }

    @Override
    protected CrudRepository<Facility, Long> getCrudRepository() {
        return facilityDao;
    }

    private List<Resident> getCommuntyResidents(Long facilityId) {
        Facility facility = facilityDao.findOne(facilityId);
        return null;
    }

}
