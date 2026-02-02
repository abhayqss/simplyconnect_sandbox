package com.scnsoft.eldermark.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.scnsoft.eldermark.converter.assessment.ComprehensiveAssessment;
import com.scnsoft.eldermark.dao.AdmitIntakeResidentDateDao;
import com.scnsoft.eldermark.dao.CcdCodeDao;
import com.scnsoft.eldermark.dao.DatabasesDao;
import com.scnsoft.eldermark.dao.ResidentDao;
import com.scnsoft.eldermark.dao.carecoordination.*;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.facades.exceptions.DatabaseNotFoundException;
import com.scnsoft.eldermark.services.carecoordination.CareCoordinationConstants;
import com.scnsoft.eldermark.services.merging.MPIService;
import com.scnsoft.eldermark.services.merging.MpiMergedResidentsService;
import com.scnsoft.eldermark.services.merging.SurvivingResidentSelector;
import com.scnsoft.eldermark.shared.ResidentFilter;
import com.scnsoft.eldermark.shared.exceptions.ResidentNotFoundException;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
public class ResidentServiceImpl implements ResidentService {

    private static final Logger logger = LoggerFactory.getLogger(ResidentServiceImpl.class);
    public static final String COMPREHENSIVE_ASSESSMENT_CODE = "COMPREHENSIVE";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ResidentDao residentDao;

    @Autowired
    private CcdCodeDao ccdCodeDao;

    @Autowired
    private MPIService mpiService;

    @Autowired
    private AssessmentDao assessmentDao;

    @Autowired
    private MpiMergedResidentsService mpiMergedResidentsService;

    @Autowired
    private SurvivingResidentSelector survivingResidentSelector;

    @Autowired
    private DatabasesDao databasesDao;

    @Autowired
    private AdmitIntakeResidentDateDao admitIntakeResidentDateDao;

    @Autowired
    private CareCoordinationResidentJpaDao careCoordinationResidentJpaDao;

    @Autowired
    private ResidentComprehensiveAssessmentJpaDao residentComprehensiveAssessmentJpaDao;

    @Autowired
    private ResidentAssessmentResultDao residentAssessmentResultDao;

    @Override
    public List<Resident> getResidents(ResidentFilter filter, Pageable pageable) {
        return residentDao.getResidents(filter, pageable);
    }

    @Override
    public List<Resident> getResidents(ResidentFilter filter) {
        return residentDao.getResidents(filter);
    }

    @Override
    public List<Resident> getMergedResidents(long residentId) {
        // get ids of all residents that ever were merged in MPI with the matchedResidents
        List<Long> mergedIds = mpiService.listMergedResidents(residentId);

        // load residents by ids
        return residentDao.getResidents(mergedIds);
    }

    private Set<Long> getFilteredDirectMergedResidentIds(Resident resident, Predicate<MpiMergedResidents> predicate) {
        Set<MpiMergedResidents> mainResidents = resident.getMainResidents();
        Set<MpiMergedResidents> secondaryResidents = resident.getSecondaryResidents();

        Iterable<MpiMergedResidents> mainResidentsFiltered = Iterables.filter(mainResidents, predicate);
        Iterable<MpiMergedResidents> secondaryResidentsFiltered = Iterables.filter(secondaryResidents, predicate);

        Set<Long> residentIds = new HashSet<Long>();
        CollectionUtils.collect(mainResidentsFiltered.iterator(), new BeanToPropertyValueTransformer("survivingResidentId"), residentIds);
        CollectionUtils.collect(secondaryResidentsFiltered.iterator(), new BeanToPropertyValueTransformer("mergedResidentId"), residentIds);

        return residentIds;
    }

    private Set<Resident> getFilteredDirectMergedResidents(Resident resident, Predicate<MpiMergedResidents> predicate) {
        Set<Long> residentIds = getFilteredDirectMergedResidentIds(resident, predicate);

        return new HashSet<>(residentDao.getResidents(residentIds));
    }

    @Override
    public Set<Resident> getDirectMergedResidents(Resident resident) {
        Predicate<MpiMergedResidents> isMerged = new Predicate<MpiMergedResidents>() {
            @Override
            public boolean apply(MpiMergedResidents input) {
                return input.isMerged();
            }
        };

        return getFilteredDirectMergedResidents(resident, isMerged);
    }

    @Override
    public Set<Long> getDirectMergedResidentIds(Resident resident) {
        Predicate<MpiMergedResidents> isMerged = new Predicate<MpiMergedResidents>() {
            @Override
            public boolean apply(MpiMergedResidents input) {
                return input.isMerged();
            }
        };

        return getFilteredDirectMergedResidentIds(resident, isMerged);
    }

    @Override
    public Set<Resident> getDirectProbablyMatchedResidents(Resident resident) {
        Predicate<MpiMergedResidents> isProbablyMatched = new Predicate<MpiMergedResidents>() {
            @Override
            public boolean apply(MpiMergedResidents input) {
                return input.isProbablyMatched() && !input.isMerged();
            }
        };

        return getFilteredDirectMergedResidents(resident, isProbablyMatched);
    }

    @Override
    public List<Resident> filterResidentsByOrganization(Collection<Long> residentIds, long organizationId) {
        return residentDao.filterResidentsByOrganization(residentIds, organizationId);
    }

    @Override
    public List<Resident> getProbablyMatchedResidents(long residentId) {
        // get ids of all residents that ever were marked as "maybe matched" in MPI with the matchedResidents
        List<Long> matchedIds = mpiMergedResidentsService.listProbablyMatchedResidents(residentId);
        // load residents by ids
        return residentDao.getResidents(matchedIds);
    }

    @Override
    public void matchAndMergeResidents(List<Long> residentIds) {
        if (CollectionUtils.isEmpty(residentIds) || residentIds.size() < 2) {
            return;
        }

        // select requested residents
        List<Resident> residents = residentDao.getResidents(residentIds);

        if (CollectionUtils.isEmpty(residents) || residents.size() < 2) {
            return; // error: employee has no access to some of these residents
        }

        Resident survivingResident = survivingResidentSelector.selectByMaxOccurrence(residents);
        residents.remove(survivingResident);

        // mark all requested residents as "matching" with the surviving resident
        // mark their matching records (if any) as "matching" with the surviving resident
        Set<Long> mergedResidentIds = new HashSet<Long>();
        for (Resident resident : residents) {
            Long id = mergeResidents(resident, survivingResident);
            mergedResidentIds.add(id);
        }

        // remove "mismatching" marks (if any) from surviving record
        if (survivingResident.getUnmergedResidentIds().removeAll(mergedResidentIds)) {
            residentDao.merge(survivingResident);
        }
    }

    private Long mergeResidents(Resident mergedResident, Resident survivingResident) {
        mpiMergedResidentsService.createOrUpdateMpiMergedResidents(mergedResident, survivingResident);  // match
        //mpiService.createOrUpdateMpi(mergedResident, survivingResident);                              // merge
        // remove "mismatching" mark (if any) from merged records
        if (mergedResident.getUnmergedResidentIds().remove(survivingResident.getId())) {
            residentDao.merge(mergedResident);
        }

        return mergedResident.getId();
    }

    @Override
    public void unmatchResidents(List<Long> residentIds, List<Long> mismatchedResidentIds) {
        if (CollectionUtils.isEmpty(residentIds) || CollectionUtils.isEmpty(mismatchedResidentIds)) {
            return;
        }

        List<Resident> mismatchedResidents = residentDao.getResidents(mismatchedResidentIds);

        if (CollectionUtils.isEmpty(mismatchedResidents)) {
            return;
        }

        for (Resident resident : mismatchedResidents) {
            /*
            if (resident.getMpi() != null) {
                Long survivingResidentId = NumberUtils.toLong(resident.getMpi().getSurvivingPatientId());
                if (survivingResidentId > 0 && residentIds.contains(survivingResidentId)) {
                    mpiService.createOrUpdateMpi(resident, null);
                }
            }
            */

            if (!CollectionUtils.isEmpty(resident.getMainResidents())) {
                Iterator<MpiMergedResidents> iterator = resident.getMainResidents().iterator();
                while (iterator.hasNext()) {
                    MpiMergedResidents mpiMergedResidents = iterator.next();
                    if (residentIds.contains(mpiMergedResidents.getSurvivingResident().getId())) {
                        mpiMergedResidentsService.deleteMpiMergedResidents(mpiMergedResidents);
                        iterator.remove();
                    }
                }
            }

            // add records to MPI_unmerged_residents
            resident.getUnmergedResidentIds().addAll(residentIds);
            resident.getUnmergedResidentIds().remove(resident.getId()); // exclude itself
            residentDao.merge(resident);
        }
    }

    @Override
    public Long getResidentCount(ResidentFilter filter) {
        return residentDao.getResidentCount(filter);
    }

    @Override
    public Resident getResident(long residentId) {
        return getResident(residentId, false);
    }
    
    @Override
    public Resident getResident(long residentId, boolean includeOptOut) {
        Resident resident = residentDao.getResident(residentId, includeOptOut);

        if (resident == null) {
            throw new ResidentNotFoundException();
        }

        return resident;
    }
    
    
    @Override
    public List<Resident> getResidents(Collection<Long> residentIds) {
        return residentDao.getResidents(residentIds);
    }

    @Override
    public Resident getResident(String legacyId, String databaseAlternativeId) {
        Database database = databasesDao.getDatabaseByAlternativeId(databaseAlternativeId);

        if (database == null) {
            throw new DatabaseNotFoundException(null);
        }

        Resident resident = residentDao.getResident(database.getId(), legacyId);

        if (resident == null) {
            throw new ResidentNotFoundException();
        }

        return resident;
    }

    @Override
    public List<Resident> getResidentsByOrganization(long organizationId) {
        return residentDao.getResidentsByOrganization(organizationId);
    }

    @Override	
    public Date getResidentArchiveDate(Long residentId,Long organizationId) {	
    	return residentDao.getResidentArchiveDate(residentId,organizationId);	
    }
    
    
    @Override
    public Resident createResident(Resident resident) {
        Resident dbResident = residentDao.create(resident);
        mpiService.createMPI(dbResident.getId(), null);
        // detach and update resident from view in order to populate hashkey
        residentDao.detach(dbResident);
        return residentDao.getResident(dbResident.getId());
    }

    @Override
    public Resident updateResident(Resident resident) {
        return residentDao.merge(resident);
    }

    @Override
    public Resident updateLegacyIds(Resident resident) {
        boolean updateNeeded = updateLegacyId(resident, CareCoordinationConstants.createLegacyId(resident));
        updateNeeded |= updateLegacyId(resident.getPerson(), CareCoordinationConstants.createLegacyId(resident.getPerson()));

        if (CollectionUtils.isNotEmpty(resident.getPerson().getAddresses())) {
            for (PersonAddress personAddress : resident.getPerson().getAddresses()) {
                String newLegacyId = CareCoordinationConstants.createLegacyIdFromParent(personAddress, resident.getPerson());
                updateNeeded |= updateLegacyId(personAddress, newLegacyId);
            }
        }
        if (CollectionUtils.isNotEmpty(resident.getPerson().getNames())) {
            for (Name name : resident.getPerson().getNames()) {
                String newLegacyId = CareCoordinationConstants.createLegacyIdFromParent(name, resident.getPerson());
                updateNeeded |= updateLegacyId(name, newLegacyId);
            }
        }
        if (CollectionUtils.isNotEmpty(resident.getPerson().getTelecoms())) {
            for (PersonTelecom telecom : resident.getPerson().getTelecoms()) {
                String newLegacyId = CareCoordinationConstants.createLegacyIdFromParent(telecom, resident.getPerson());
                updateNeeded |= updateLegacyId(telecom, newLegacyId);
            }
        }
        if (updateNeeded) {
            return updateResident(resident);
        }
        return resident;
    }

    private boolean updateLegacyId(StringLegacyIdAwareEntity entity, String newLegacyId) {
        if (!StringUtils.equalsIgnoreCase(newLegacyId, entity.getLegacyId())) {
            entity.setLegacyId(newLegacyId);
            return true;
        }
        return false;
    }


    @Override
    public void deleteResident(Resident resident) {
        if (resident == null) {
            return;
        }
        mpiService.deleteMPIByResidentId(resident.getId());
        residentDao.delete(resident);
        residentDao.flush();
    }

    @Override
    public List<AdmitIntakeResidentDate> getAdmitIntakeHistoryFiltered(Long residentId) {
        final Sort sort = new Sort(Sort.Direction.DESC, "admitIntakeDate");
        return admitIntakeResidentDateDao.getAllByResidentId(residentId, sort);
    }

    @Override
    public Resident convert(final CareCoordinationResident careCoordinationResident) {
        return residentDao.get(careCoordinationResident.getId());
    }

    @Override
    public Resident getResidentByIdentityFields(Long organizationId, String ssn, Date dateOfBirth, String lastName, String firstName) {
        return residentDao.getResidentByIdentityFields(organizationId, ssn, dateOfBirth, lastName, firstName);
    }

    @Override
    public Optional<Resident> getResidentByIdentityFields(Long organizationId, Long communityId, String residentLegacyId) {
        return Optional.fromNullable(residentDao.getResidentByIdentityFields(organizationId, communityId, residentLegacyId));
    }

    @Override
    public void updateResidentAccordingToComprehensiveAssessment(Long assessmentId, Long residentAssessmentResultId) {
        logger.info("[ResidentAssessmentResultServiceImpl] Assessment id is {}", assessmentId);
        Assessment assessment = assessmentDao.getOne(assessmentId);
        ResidentAssessmentResult residentAssessmentResult = residentAssessmentResultDao.findOne(residentAssessmentResultId);

        Resident resident = residentAssessmentResult.getResident();
        if (COMPREHENSIVE_ASSESSMENT_CODE.equals(assessment.getCode())){
            try {
                ComprehensiveAssessment comprehensiveAssessment = objectMapper.readValue(residentAssessmentResult.getResult(), ComprehensiveAssessment.class);

                if (resident.getMaritalStatus() == null){
                    CcdCode maritalStatusCode = convertMaritalStatus(comprehensiveAssessment);
                    logger.info("[ResidentAssessmentResultServiceImpl] Marital status Ccd code : {}", maritalStatusCode);
                    if (maritalStatusCode != null){
                        careCoordinationResidentJpaDao.updateMaritalStatus(maritalStatusCode, resident.getId());
                    }
                }

                if (resident.getGender() == null){
                    CcdCode genderStatusCode = convertGender(comprehensiveAssessment);
                    logger.info("[ResidentAssessmentResultServiceImpl] Gender Ccd code : {}", genderStatusCode);
                    if (genderStatusCode != null){
                        careCoordinationResidentJpaDao.updateGender(genderStatusCode, resident.getId());
                    }
                }

                String pcpFirstName = comprehensiveAssessment.getPrimaryCarePhysicianFirstName();
                String pcpLastName = comprehensiveAssessment.getPrimaryCarePhysicianLastName();
                if (isNotBlank(pcpFirstName) || isNotBlank(pcpLastName)){
                    logger.info("[ResidentAssessmentResultServiceImpl] Primary care physician first name : {}, primary care physician last name: {}",
                            comprehensiveAssessment.getPrimaryCarePhysicianFirstName(),
                            comprehensiveAssessment.getPrimaryCarePhysicianLastName());

                    ResidentComprehensiveAssessment residentComprehensiveAssessment = new ResidentComprehensiveAssessment();
                    residentComprehensiveAssessment.setPrimaryCarePhysicianFirstName(pcpFirstName);
                    residentComprehensiveAssessment.setPrimaryCarePhysicianLastName(pcpLastName);
                    residentComprehensiveAssessment.setResidentAssessmentResult(residentAssessmentResult);
                    residentComprehensiveAssessment.setResidentId(resident.getId());
                    residentComprehensiveAssessmentJpaDao.saveAndFlush(residentComprehensiveAssessment);
                }

            } catch (IOException ex){
                logger.error("Error parsing comprehensive json result : {}", ex);
            }
        }

    }

    private CcdCode convertMaritalStatus(ComprehensiveAssessment comprehensiveAssessment){
        if (comprehensiveAssessment.getMaritalStatus() != null){
            logger.info("[ResidentAssessmentResultServiceImpl] Marital status from comprehensive assessment {}", comprehensiveAssessment.getMaritalStatus());
            MaritalStatusType maritalStatus = MaritalStatusType.fromAssessmentValue(comprehensiveAssessment.getMaritalStatus());
            return maritalStatus != null ? ccdCodeDao.getCcdCode(maritalStatus.getCcdCode(), CodeSystem.MARITAL_STATUS.getOid()) : null;
        }
        return null;
    }

    private CcdCode convertGender(ComprehensiveAssessment comprehensiveAssessment){
        if (comprehensiveAssessment.getGender() != null){
            logger.info("[ResidentAssessmentResultServiceImpl] Gender from comprehensive assessment {}", comprehensiveAssessment.getGender());
            GenderType type = GenderType.fromAssessmentValue(comprehensiveAssessment.getGender());
            return ccdCodeDao.getCcdCode(type.getCcdCode(), CodeSystem.ADMINISTRATIVE_GENDER.getOid());
        }
        return null;
    }

}
