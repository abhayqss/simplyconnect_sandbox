package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.PhysicianDao;
import com.scnsoft.eldermark.entity.phr.InNetworkInsurance;
import com.scnsoft.eldermark.entity.phr.Physician;
import com.scnsoft.eldermark.entity.phr.PhysicianCategory;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.web.entity.PhysicianExtendedDto;
import com.scnsoft.eldermark.web.entity.ProfessionalProfileDto;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author phomal
 * Created on 2/15/2017
 */
@Service
@Transactional(readOnly = true)
public class PhysiciansService {

    private final PhysicianDao physicianDao;
    private DozerBeanMapper dozer;

    @Autowired
    public PhysiciansService(PhysicianDao physicianDao) {
        this.physicianDao = physicianDao;
    }

    public PhysicianExtendedDto get(Long physicianId) {
        return convert(getPhysicianOrThrow(physicianId));
    }

    Physician getPhysicianOrThrow(Long physicianId) {
        Physician physician = physicianDao.findByIdAndDiscoverableTrueAndVerifiedTrue(physicianId);
        if (physician == null)
            throw new PhrException(PhrExceptionType.PHYSICIAN_NOT_FOUND);
        return physician;
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

    ProfessionalProfileDto transformProfessionalInfo(Physician physician) {
        ProfessionalProfileDto profileDto = dozer.map(physician, ProfessionalProfileDto.class);
        profileDto.setSpecialities(transformSpecialitiesToStrings(physician.getCategories()));
        profileDto.setInNetworkInsurances(transformInsurancesToStrings(physician.getInNetworkInsurances()));
        return profileDto;
    }

    private static List<String> transformSpecialitiesToStrings(Set<PhysicianCategory> categories) {
        List<String> categoriesList = new ArrayList<>();
        CollectionUtils.collect(categories, new BeanToPropertyValueTransformer("displayName"), categoriesList);
        return categoriesList;
    }

    private static List<String> transformInsurancesToStrings(Set<InNetworkInsurance> insurances) {
        List<String> insurancesList = new ArrayList<>();
        CollectionUtils.collect(insurances, new BeanToPropertyValueTransformer("displayName"), insurancesList);
        return insurancesList;
    }

    @Autowired
    public void setDozer(DozerBeanMapper dozer) {
        this.dozer = dozer;
    }

}
