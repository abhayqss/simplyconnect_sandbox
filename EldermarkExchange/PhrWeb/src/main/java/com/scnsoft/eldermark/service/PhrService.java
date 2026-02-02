package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.ResidentDao;
import com.scnsoft.eldermark.entity.Guardian;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.facades.ccd.CcdFacade;
import com.scnsoft.eldermark.shared.ccd.CcdHeaderPatientDto;
import com.scnsoft.eldermark.shared.ccd.GuardianDto;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import com.scnsoft.eldermark.web.security.PhrSecurityUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author averazub
 * @author phomal
 * Created by averazub on 1/3/2017.
 */
@Service
@Transactional(readOnly = true)
public class PhrService extends BasePhrService {

    @Autowired
    private CcdFacade ccdFacade;

    @Autowired
    private ResidentDao residentDao;

    @Autowired
    private DozerBeanMapper dozerBeanMapper;

    @Autowired
    private CareTeamSecurityUtils careTeamSecurityUtils;

    public CcdHeaderPatientDto getUserDemographics(Long userId, boolean includeGuardians) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId);

        // we don't merge demographics data
        Long residentId = getResidentIdOrThrow(userId);
        final boolean showSsn = PhrSecurityUtils.checkAccessToUserInfo(userId);
        CcdHeaderPatientDto dto = ccdFacade.getCcdHeaderPatient(residentId, showSsn);
        if (!includeGuardians) {
            dto.setGuardians(null);
        }

        return dto;
    }

    public List<GuardianDto> getUserGuardiansInfo(Long userId) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId);

        // we don't merge demographics data
        Long residentId = getResidentIdOrThrow(userId);
        Resident resident = residentDao.get(residentId);

        List<GuardianDto> resultList = new ArrayList<>();
        List<Guardian> sourceList = resident.getGuardians();
        for (Guardian source : sourceList) {
            resultList.add(dozerBeanMapper.map(source, GuardianDto.class));
        }
        return resultList;
    }

    public void setDozer(DozerBeanMapper dozer) {
        this.dozerBeanMapper = dozer;
    }

}
