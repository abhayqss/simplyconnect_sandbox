package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.dao.PhysicianDao;
import com.scnsoft.eldermark.api.external.entity.Physician;
import com.scnsoft.eldermark.api.external.entity.PhysicianCategory;
import com.scnsoft.eldermark.api.external.web.dto.PhysicianExtendedDto;
import com.scnsoft.eldermark.api.external.web.dto.ProfessionalProfileDto;
import com.scnsoft.eldermark.api.shared.exception.PhrException;
import com.scnsoft.eldermark.api.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.entity.InNetworkInsurance;
import org.apache.commons.collections4.CollectionUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PhysiciansServiceImpl implements PhysiciansService {

    private final PhysicianDao physicianDao;
    private DozerBeanMapper dozer;

    @Autowired
    public PhysiciansServiceImpl(PhysicianDao physicianDao) {
        this.physicianDao = physicianDao;
    }

    @Override
    public PhysicianExtendedDto get(Long physicianId) {
        return convert(getPhysicianOrThrow(physicianId));
    }

    Physician getPhysicianOrThrow(Long physicianId) {
        return physicianDao.findByIdAndDiscoverableTrueAndVerifiedTrue(physicianId)
                .orElseThrow(() -> new PhrException(PhrExceptionType.PHYSICIAN_NOT_FOUND));
    }

    private PhysicianExtendedDto convert(Physician physician) {
        PhysicianExtendedDto dto = new PhysicianExtendedDto();
        dto.setId(physician.getId());
        dto.setFullName(physician.getEmployee().getFullName());
        if (physician.getEmployee().getCareTeamRole() != null) {
            dto.setSpeciality(physician.getEmployee().getCareTeamRole().getName());
        }

        ProfessionalProfileDto profileDto = transformProfessionalInfo(physician);
        dto.setProfessionalInfo(profileDto);

        return dto;
    }

    private ProfessionalProfileDto transformProfessionalInfo(Physician physician) {
        ProfessionalProfileDto profileDto = dozer.map(physician, ProfessionalProfileDto.class);
        profileDto.setSpecialities(categoriesNames(physician.getCategories()));
        profileDto.setInNetworkInsurances(insurancesNames(physician.getInNetworkInsurances()));
        return profileDto;
    }

    private static List<String> categoriesNames(Set<PhysicianCategory> categories) {
        return CollectionUtils.emptyIfNull(categories).stream()
                .map(PhysicianCategory::getDisplayName)
                .collect(Collectors.toList());
    }

    private static List<String> insurancesNames(Set<InNetworkInsurance> insurances) {
        return CollectionUtils.emptyIfNull(insurances).stream()
                .map(InNetworkInsurance::getDisplayName)
                .collect(Collectors.toList());
    }

    @Autowired
    public void setDozer(DozerBeanMapper dozer) {
        this.dozer = dozer;
    }

}
