package com.scnsoft.eldermark.facades.ccd;

import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.dao.ResidentDao;
import com.scnsoft.eldermark.dao.ccd.CcdSectionDaoFactory;
import com.scnsoft.eldermark.dao.ccd.CcdSecurityUtils;
import com.scnsoft.eldermark.entity.CcdHeaderDetails;
import com.scnsoft.eldermark.entity.CcdSection;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.ccd.section.CcdSectionService;
import com.scnsoft.eldermark.services.cda.CcdHeaderDetailsService;
import com.scnsoft.eldermark.services.merging.MPIService;
import com.scnsoft.eldermark.shared.ccd.*;
import com.scnsoft.eldermark.shared.exceptions.BusinessAccessDeniedException;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;


@Component
@Transactional(readOnly = true)
public class CcdFacadeImpl implements CcdFacade {

    @Autowired
    private CcdHeaderDetailsService ccdService;

    @Autowired
    private CcdSectionDaoFactory ccdSectionDaoFactory;

    @Autowired
    private MPIService mpiService;

    @Autowired
    private DozerBeanMapper mapper;

    @Autowired
    private ResidentDao residentDao;

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    List<CcdSectionService> ccdSectionServices;

    private final Map<String, Class<? extends CcdSectionDto>> SECTION_NAME_TO_CLASS;

    {
        SECTION_NAME_TO_CLASS = new HashMap<>(CcdSection.values().length);
        SECTION_NAME_TO_CLASS.put(CcdSection.ALLERGIES.getName(), AllergySectionDto.class);
        SECTION_NAME_TO_CLASS.put(CcdSection.MEDICATIONS.getName(), MedicationSectionDto.class);
        SECTION_NAME_TO_CLASS.put(CcdSection.PROBLEMS.getName(), ProblemSectionDto.class);
        SECTION_NAME_TO_CLASS.put(CcdSection.PROCEDURES.getName(), ProcedureSectionDto.class);
        SECTION_NAME_TO_CLASS.put(CcdSection.RESULTS.getName(), ResultSectionDto.class);
        SECTION_NAME_TO_CLASS.put(CcdSection.ENCOUNTERS.getName(), EncounterSectionDto.class);
        SECTION_NAME_TO_CLASS.put(CcdSection.ADVANCE_DIRECTIVES.getName(), AdvanceDirectiveSectionDto.class);
        SECTION_NAME_TO_CLASS.put(CcdSection.FAMILY_HISTORY.getName(), FamilyHistorySectionDto.class);
        SECTION_NAME_TO_CLASS.put(CcdSection.VITAL_SIGNS.getName(), VitalSignsSectionDto.class);
        SECTION_NAME_TO_CLASS.put(CcdSection.IMMUNIZATIONS.getName(), ImmunizationSectionDto.class);
        SECTION_NAME_TO_CLASS.put(CcdSection.PAYER_PROVIDERS.getName(), PayerProviderSectionDto.class);
        SECTION_NAME_TO_CLASS.put(CcdSection.MEDICAL_EQUIPMENT.getName(), MedicalEquipmentSectionDto.class);
        SECTION_NAME_TO_CLASS.put(CcdSection.SOCIAL_HISTORY.getName(), SocialHistorySectionDto.class);
        SECTION_NAME_TO_CLASS.put(CcdSection.PLAN_OF_CARE.getName(), PlanOfCareSectionDto.class);
    }

    @Override
    public <T extends CcdSectionDto> List<T> getCcdSectionDto(String sectionName, Long residentId, Pageable pageable, Boolean aggregated) {
        //todo security
        Class dtoClazz = SECTION_NAME_TO_CLASS.get(sectionName);
        return ccdSectionDaoFactory.getCcdSectionDao(dtoClazz).getSectionDto(residentId, pageable, aggregated);
    }

    @Override
    public long getCcdSectionDtoCount(String sectionName, Long residentId, Boolean aggregated) {
        //todo security
        Class<? extends CcdSectionDto> sectionDtoClass = SECTION_NAME_TO_CLASS.get(sectionName);
        return ccdSectionDaoFactory.getCcdSectionDao(sectionDtoClass).getSectionDtoCount(residentId, aggregated);
    }

    @Override
    public CcdHeaderDetailsDto getCcdHeaderDetails(Long residentId, Boolean aggregated) {
        //todo security
        final CcdHeaderDetails dbo;
        if (TRUE.equals(aggregated)) {
            final List<Long> ids = mpiService.listMergedResidents(residentId);
            dbo = ccdService.getHeaderDetails(residentId, ids);
        } else {
            dbo = ccdService.getHeaderDetails(residentId);
        }
        return mapper.map(dbo, CcdHeaderDetailsDto.class);
    }

    @Override
    public CcdHeaderPatientDto getCcdHeaderPatient(Long residentId, boolean showSsn) {
        //todo security
        Resident dbo = ccdService.getRecordTarget(residentId);
        CcdHeaderPatientDto dto = mapper.map(dbo, CcdHeaderPatientDto.class);
        if (!showSsn) {
            dto.setSocialSecurity("###-##-" + dbo.getSsnLastFourDigits());
        }
        return dto;
    }

    @Override
    public boolean validateSectionName(String sectionName) {
        return SECTION_NAME_TO_CLASS.containsKey(sectionName);
    }

    @Override
    public boolean canAddCcd(Long residentId) {
        return canAddCcd(residentDao.get(residentId));
    }

    @Override
    public boolean canEditCcd(Long residentId) {
        return canEditCcd(residentDao.get(residentId));
    }

    @Override
    public boolean canViewCcd(Long residentId) {
        return canViewCcd(residentDao.get(residentId));
    }

    @Override
    public boolean canDeleteCcd(Long residentId) {
        return canDeleteCcd(residentDao.get(residentId));
    }

    @Override
    public boolean canAddCcd(Resident resident) {
        //todo load merged residents
        return CcdSecurityUtils.canAddCcd(SecurityUtils.getAuthenticatedUser(),
                SecurityUtils.getAuthenticatedUser().getEmployeeAndLinkedEmployeeIds(),
                Collections.singletonList(resident));
    }

    @Override
    public boolean canEditCcd(Resident resident) {
        //todo load merged residents
        return CcdSecurityUtils.canEditCcd(SecurityUtils.getAuthenticatedUser(),
                SecurityUtils.getAuthenticatedUser().getEmployeeAndLinkedEmployeeIds(),
                Collections.singletonList(resident));
    }

    @Override
    public boolean canViewCcd(Resident resident) {
        //todo load merged residents
        return CcdSecurityUtils.canViewCcd(SecurityUtils.getAuthenticatedUser(),
                SecurityUtils.getAuthenticatedUser().getEmployeeAndLinkedEmployeeIds(),
                Collections.singletonList(resident));
    }

    @Override
    public boolean canDeleteCcd(Resident resident) {
        //todo load merged residents
        return CcdSecurityUtils.canDeleteCcd(SecurityUtils.getAuthenticatedUser(),
                SecurityUtils.getAuthenticatedUser().getEmployeeAndLinkedEmployeeIds(),
                Collections.singletonList(resident));
    }

    @Override
    public void canAddCcdOrThrow(Long residentId) {
        if (!canAddCcd(residentId)) {
            throw new BusinessAccessDeniedException("You can not add ccd record for the patient with id=" + residentId);
        }
    }

    @Override
    public void canEditCcdOrThrow(Long residentId) {
        if (!canEditCcd(residentId)) {
            throw new BusinessAccessDeniedException("You can not edit ccd record for the patient with id=" + residentId);
        }
    }

    @Override
    public void canViewCcdOrThrow(Long residentId) {
        if (!canViewCcd(residentId)) {
            throw new BusinessAccessDeniedException("You can not view ccd record for the patient with id=" + residentId);
        }
    }

    @Override
    public void canDeleteCcdOrThrow(Long residentId) {
        if (!canDeleteCcd(residentId)) {
            throw new BusinessAccessDeniedException("You can not delete ccd record for the patient with id=" + residentId);
        }
    }

    @Override
    public String findFreeTextBySectionAndId(String sectionName, Long id) {
        //todo security
        for (CcdSectionService service : ccdSectionServices) {
            if (service.getSection().getName().equals(sectionName)) {
                return service.getFreeTextById(id);
            }
        }
        return null;
    }

}
