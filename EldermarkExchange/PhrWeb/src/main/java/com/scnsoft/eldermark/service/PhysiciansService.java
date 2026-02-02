package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.InNetworkInsuranceDao;
import com.scnsoft.eldermark.dao.phr.PhysicianCategoryDao;
import com.scnsoft.eldermark.dao.phr.PhysicianDao;
import com.scnsoft.eldermark.entity.phr.InNetworkInsurance;
import com.scnsoft.eldermark.entity.phr.Physician;
import com.scnsoft.eldermark.entity.phr.PhysicianCategory;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.web.entity.*;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author phomal
 * Created on 5/2/2017
 */
@Service
@Transactional
public class PhysiciansService extends BasePhrService {

    @Autowired
    PhysicianDao physicianDao;

    @Autowired
    PhysicianCategoryDao physicianCategoryDao;

    @Autowired
    InNetworkInsuranceDao inNetworkInsuranceDao;

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private DozerBeanMapper dozer;

    public PhysicianExtendedDto getPhysician(Long physicianId) {
        return transform(getPhysicianOrThrow(physicianId));
    }

    public List<PhysicianDto> listPhysicians() {
        List<Physician> physicians = physicianDao.findAllByDiscoverableTrueAndVerifiedTrue();
        return transform(physicians);
    }

    Physician getPhysicianOrThrow(Long physicianId) {
        Physician physician = physicianDao.findByIdAndDiscoverableTrueAndVerifiedTrue(physicianId);
        if (physician == null)
            throw new PhrException(PhrExceptionType.PHYSICIAN_NOT_FOUND);
        return physician;
    }

    Physician getPhysicianByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        return physicianDao.getByUserMobileId(userId);
    }

    public List<SpecialityDto> listPhysicianSpecialities() {
        List<PhysicianCategory> categories = physicianCategoryDao.findAll();
        return transformCategories(categories);
    }

    public List<InsuranceDto> listPhysicianInsurances() {
        List<InNetworkInsurance> insurances = inNetworkInsuranceDao.findAll();
        return transformInsurances(insurances);
    }

    Set<PhysicianCategory> getSpecialitiesById(Iterable<Long> ids) {
        return new HashSet<>(physicianCategoryDao.findAll(ids));
    }

    Set<InNetworkInsurance> getInsurancesById(Iterable<Long> ids) {
        return new HashSet<>(inNetworkInsuranceDao.findAll(ids));
    }

    Physician create(Physician physician) {
        return physicianDao.saveAndFlush(physician);
    }

    // Transformers

    private List<SpecialityDto> transformCategories(List<PhysicianCategory> categories) {
        List<SpecialityDto> dtos = new ArrayList<>();
        for (PhysicianCategory category : categories) {
            dtos.add(dozer.map(category, SpecialityDto.class));
        }
        return dtos;
    }

    private List<InsuranceDto> transformInsurances(List<InNetworkInsurance> insurances) {
        List<InsuranceDto> dtos = new ArrayList<>();
        for (InNetworkInsurance insurance : insurances) {
            dtos.add(dozer.map(insurance, InsuranceDto.class));
        }
        return dtos;
    }

    private PhysicianExtendedDto transform(Physician physician) {
        User userMobile = physician.getUserMobile();

        PhysicianExtendedDto dto = new PhysicianExtendedDto();
        dto.setId(physician.getId());
        dto.setUserId(physician.getUserMobile().getId());
        dto.setFullName(userMobile.getEmployeeFullName());
        dto.setPhotoUrl(avatarService.getPhotoUrl(userMobile.getId()));
        if (userMobile.getEmployee().getCareTeamRole() != null) {
            dto.setSpeciality(userMobile.getEmployee().getCareTeamRole().getName());
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

    private List<PhysicianDto> transform(List<Physician> physicians) {
        List<PhysicianDto> dtos = new ArrayList<>();
        for (Physician physician : physicians) {
            dtos.add(transformListItem(physician));
        }
        return dtos;
    }

    PhysicianDto transformListItem(Physician physician) {
        User userMobile = physician.getUserMobile();
        PhysicianDto dto = new PhysicianDto();
        dto.setId(physician.getId());
        dto.setUserId(userMobile.getId());
        dto.setFullName(physician.getEmployee().getFullName());
        dto.setPhotoUrl(avatarService.getPhotoUrl(physician.getUserMobile().getId()));
        if (physician.getEmployee().getCareTeamRole() != null) {
            dto.setSpeciality(physician.getEmployee().getCareTeamRole().getName());
        }

        return dto;
    }

    public void setDozer(DozerBeanMapper dozer) {
        this.dozer = dozer;
    }
}
