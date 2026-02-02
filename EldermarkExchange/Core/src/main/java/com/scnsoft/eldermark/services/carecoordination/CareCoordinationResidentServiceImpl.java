package com.scnsoft.eldermark.services.carecoordination;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.dao.CcdCodeDao;
import com.scnsoft.eldermark.dao.JpaAdmittanceHistoryDao;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.dao.ResidentDao;
import com.scnsoft.eldermark.dao.carecoordination.CareCoordinationOrganizationDaoCustomImpl;
import com.scnsoft.eldermark.dao.carecoordination.CareCoordinationResidentDao;
import com.scnsoft.eldermark.dao.carecoordination.OrganizationCareTeamMemberDao;
import com.scnsoft.eldermark.dao.exceptions.NotUniqueValueException;
import com.scnsoft.eldermark.dao.marketplace.InsurancePlanDao;
import com.scnsoft.eldermark.dao.phr.InNetworkInsuranceDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.schema.Patient;
import com.scnsoft.eldermark.services.StateService;
import com.scnsoft.eldermark.services.exceptions.BusinessException;
import com.scnsoft.eldermark.services.merging.MPIService;
import com.scnsoft.eldermark.shared.Gender;
import com.scnsoft.eldermark.shared.carecoordination.*;
import com.scnsoft.eldermark.shared.carecoordination.contacts.LinkedContactDto;
import com.scnsoft.eldermark.shared.carecoordination.patients.PatientListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.patients.PatientsFilterDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.CareCoordinationUtils;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.shared.exceptions.BusinessAccessDeniedException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author pzhurba
 * @author averazub
 * @author knetkachou
 * @author phomal
 * Created on 23-Oct-15.
 */
@Service
public class CareCoordinationResidentServiceImpl implements CareCoordinationResidentService {
    private static final Logger logger = LoggerFactory.getLogger(CareCoordinationResidentServiceImpl.class);

    @Autowired
    CareCoordinationResidentDao careCoordinationResidentDao;

    @Autowired
    CcdCodeDao ccdCodeDao;

    @Autowired
    OrganizationDao organizationDao;

    @Autowired
    AddressService addressService;

    @Autowired
    MPIService mpiService;

    @Autowired
    StateService stateService;

    @Autowired
    ResidentMatcherService residentMatcherService;

    @Autowired
    CareCoordinationOrganizationDaoCustomImpl careCoordinationOrganizationDaoCustom;

    @Autowired
    CareTeamService careTeamService;

    @Autowired
    ResidentDeviceService residentDeviceService;

    @Autowired
    CareTeamRoleService careTeamRoleService;

    @Autowired
    OrganizationCareTeamMemberDao organizationCareTeamMemberDao;

    @Autowired
    ResidentDao residentDao;

    @Autowired
    private InNetworkInsuranceDao inNetworkInsuranceDao;

    @Autowired
    private InsurancePlanDao insurancePlanDao;

    @Autowired
    private EventService eventService;

    @Autowired
    private JpaAdmittanceHistoryDao admittanceHistoryDao;

    @Override
    public List<KeyValueDto> getResidentsNamesForEmployee(Set<Long> employeeIds) {
        Long databaseId = SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId();
        List<Long> communityIds = SecurityUtils.getAuthenticatedUser().getCurrentCommunityIds();
        boolean isAdmin = SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR);
        Pair<Boolean, Set<Long>> adminAndEmployeeIds = getCommunityAdminEmployeeIds(employeeIds);
        Set<Long> employeeCommunityIds = adminAndEmployeeIds.getSecond();
        isAdmin = isAdmin || adminAndEmployeeIds.getFirst();
        return careCoordinationResidentDao.getResidentNamesForEmployee(employeeIds, databaseId, communityIds, isAdmin, employeeCommunityIds);
    }


    @Override
    public Page<PatientListItemDto> getPatientListItemDtoForEmployee(Set<Long> employeeIds, PatientsFilterDto filter, Pageable pageable) {
        Long databaseId = SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId();
        List<Long> communityIds = SecurityUtils.getAuthenticatedUser().getCurrentCommunityIds();
        boolean isAdmin = SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR);
        Pair<Boolean, Set<Long>> adminAndEmployeeIds = getCommunityAdminEmployeeIds(employeeIds);
        Set<Long> employeeCommunityIds = adminAndEmployeeIds.getSecond();
        isAdmin = isAdmin || adminAndEmployeeIds.getFirst();
        long startTime = System.currentTimeMillis();
        List<PatientListItemDto> result = careCoordinationResidentDao.getResidentsForEmployee(employeeIds, filter, databaseId, communityIds, pageable, isAdmin, employeeCommunityIds);
        long stopTime = System.currentTimeMillis();
        System.out.println("careCoordinationResidentDao.getResidentsForEmployee:" + (stopTime - startTime) + "ms");
        //recalculate counts taking into account not viewable
        if (!isAdmin) {
            Map<Long, Long> eventCountsForResident = eventService.countEventsForEachResidentIdForNonAdminEmployees(employeeIds, databaseId, new HashSet<>(communityIds), employeeCommunityIds);
            if (MapUtils.isNotEmpty(eventCountsForResident) && CollectionUtils.isNotEmpty(result)) {
                for (PatientListItemDto patientListItemDto : result) {
                    if (eventCountsForResident.containsKey(patientListItemDto.getId())) {
                        patientListItemDto.setEventCount(eventCountsForResident.get(patientListItemDto.getId()));
                    }
                }
            }
        }
        return new PageImpl<>(result, pageable, careCoordinationResidentDao.getResidentsForEmployeeCount(employeeIds, filter, databaseId, communityIds, isAdmin, employeeCommunityIds));
    }

    /**
     * checks if employee can view all events related to organization (isAdmin)
     * or related to specific communities in case COMMUNITY_ADMIN role is present or specific communities from primary organization are accessible(employeeCommunityIds)
     *
     * @param employeeIds
     * @return
     */
    @Override
    public Pair<Boolean, Set<Long>> getCommunityAdminEmployeeIds(Set<Long> employeeIds) {
        Set<Long> employeeCommunityIds = null;
        Boolean isAdmin = false;
        if (!SecurityUtils.isAffiliatedView()) {
            employeeCommunityIds = new HashSet<Long>();
            for (Long employeeId : employeeIds) {
                Set<GrantedAuthority> currentEmployeeAuthorities = SecurityUtils.getAuthenticatedUser().getEmployeeAuthoritiesMap().get(employeeId);
                Long employeeCommunityId = SecurityUtils.getAuthenticatedUser().getLinkedEmployeeById(employeeId).getCommunityId();
                if (SecurityUtils.hasRole(currentEmployeeAuthorities, CareTeamRoleCode.COMMUNITY_ADMINISTRATOR)) {
                    employeeCommunityIds.add(employeeCommunityId);
                } else if (SecurityUtils.hasRole(currentEmployeeAuthorities, CareTeamRoleCode.ADMINISTRATOR)) {
                    isAdmin = isAdmin || (SecurityUtils.getAuthenticatedUser().getLinkedEmployeeById(employeeId).getDatabaseId().equals(SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId()));
                }
            }
        } else {
            employeeCommunityIds = new HashSet<Long>();
            for (Long employeeId : employeeIds) {
                Set<GrantedAuthority> currentEmployeeAuthorities = SecurityUtils.getAuthenticatedUser().getEmployeeAuthoritiesMap().get(employeeId);
                if (SecurityUtils.hasAnyRole(currentEmployeeAuthorities, CareTeamRoleCode.ROLES_CAN_VIEW_ALL_PRIMARY_PATIENTS_EVENTS)) {
                    LinkedContactDto linkedContact = SecurityUtils.getAuthenticatedUser().getLinkedEmployeeById(employeeId);
                    List<AffiliatedOrganizationDto> primaryOrgs = careCoordinationOrganizationDaoCustom.getPrimaryAffiliatedOrganizationInfo(SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId(), linkedContact.getDatabaseId());
                    Long linkedCommunityId = linkedContact.getCommunityId();
                    Long linkedDatabaseId = linkedContact.getDatabaseId();
                    for (AffiliatedOrganizationDto prOrg : primaryOrgs) {
                        boolean communityAdminAndHasAccess = (SecurityUtils.hasAnyRole(currentEmployeeAuthorities, CareTeamRoleCode.COMMUNITY_ADMINISTRATOR))
                                && (prOrg.getAffiliatedCommunityId() == null || prOrg.getAffiliatedCommunityId().equals(linkedCommunityId));
                        boolean adminAndHasAccess = (SecurityUtils.hasAnyRole(currentEmployeeAuthorities, CareTeamRoleCode.ADMINISTRATOR))
                                && (prOrg.getAffiliatedOrganizationId().equals(linkedDatabaseId));
                        if (communityAdminAndHasAccess || adminAndHasAccess) {
                            //in case all communities are shared with COMMUNITY_ADMINISTRATOR OR ADMINISTRATOR - all patients will be visible
                            if (prOrg.getPrimaryCommunityId() == null) {
                                return new Pair<Boolean, Set<Long>>(Boolean.TRUE, null);
                            } else {
                                employeeCommunityIds.add(prOrg.getPrimaryCommunityId());
                            }
                        }
                    }
                }
            }
        }
        return new Pair<Boolean, Set<Long>>(isAdmin, employeeCommunityIds);
    }

    @Override
    public List<Long> getResidentsIdsForEmployee(Long employeeId) {
        Set<Long> employeeIdsList = new HashSet<Long>();
        employeeIdsList.add(employeeId);
        Long databaseId = SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId();
        List<Long> communityIds = SecurityUtils.getAuthenticatedUser().getCurrentCommunityIds();
        boolean isAdmin = SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR);
        Pair<Boolean, Set<Long>> adminAndEmployeeIds = getCommunityAdminEmployeeIds(employeeIdsList);
        Set<Long> employeeCommunityIds = adminAndEmployeeIds.getSecond();
        isAdmin = isAdmin || adminAndEmployeeIds.getFirst();
        List<Long> residentIds = careCoordinationResidentDao.getResidentIdsForEmployee(employeeIdsList, databaseId, communityIds, isAdmin, employeeCommunityIds);
//        Set<Long> result = new HashSet<Long>();
//        if(!CollectionUtils.isEmpty(residents)) {
//            for (PatientListItemDto patientListItemDto : residents) {
//                result.add(patientListItemDto.getId());
//            }
//        }
        return residentIds;
    }
//
//    private CareCoordinationResident findFullMatchedResident (Long organizationId, PatientDtoExtended patient, List<CareCoordinationResident> patientDtoList) {
//        for (CareCoordinationResident foundPatient: patientDtoList) {
//            if (patient.getSsn().equals(foundPatient.getSocialSecurity()) && matchStrings(patient.getFirstName(), foundPatient.getFirstName()) &&
//                    matchStrings(patient.getLastName(), foundPatient.getLastName()) && foundPatient.getFacility().getId().equals(organizationId)) {
//                return foundPatient;
//            }
//        }
//        return null;
//    }
//
//    private boolean matchStrings (String str1, String str2){
//
//        return Levenshtein.distance(str1, str2)<=1;
//    }

    // TODO this method is overcomplicated. refactoring or javadoc is very welcome
    @Override
    public List<CareCoordinationResident> getOrCreateResident(Long organizationId, Patient patient) {
        if (patient.getSSN() != null) patient.setSSN(patient.getSSN().replaceAll("-", ""));
        final Organization organization = organizationDao.getOrganization(organizationId);
        Long idFoundByPatientIdInOrg = getIdByPatientIdInOrganization(patient.getPatientId(), organization.getDatabase().getId());
        Long idFoundByPatientIdInCommunity = getIdByPatientIdInCommunity(patient.getPatientId(), organization.getId());
        List<CareCoordinationResident> foundResident;
        if (idFoundByPatientIdInOrg != null && idFoundByPatientIdInCommunity != null) {
            //In case patient was found in organization and community specified in request - update patient data
            CareCoordinationResident careCoordinationResident = careCoordinationResidentDao.get(idFoundByPatientIdInOrg);
            careCoordinationResident = updatePatientData(patient, careCoordinationResident);
            foundResident = Arrays.asList(careCoordinationResident);
        } else if (idFoundByPatientIdInOrg != null && idFoundByPatientIdInCommunity == null) {
            //In case patient was found in organization but in other community - do not update id or data
            foundResident = careCoordinationResidentDao.findCareCoordinationResident(new CareCoordinationResidentFilter(patient), organization);
        } else {
            //In case patient with specified id is not exist - update patient id
            foundResident = careCoordinationResidentDao.findCareCoordinationResident(new CareCoordinationResidentFilter(patient), organization);
            updateLegacyId(patient.getPatientId(), foundResident);
        }
        List<CareCoordinationResident> residents = residentMatcherService.findFullMatchedResidents(convert(patient), organizationId, null);
        if (!checkExistInList(foundResident, residents)) {
            residents.addAll(foundResident);
        }
        if (CollectionUtils.isEmpty(residents)) {
            CareCoordinationResident resident = createOrUpdateCareCoordinationResident(patient, organization, null, false, idFoundByPatientIdInOrg != null);
            return Arrays.asList(resident);
        }
        return residents;
    }

    private boolean patientsBasicDataEqual(Patient patient, CareCoordinationResident careCoordinationResident) {
        return patient.getSSN().equalsIgnoreCase(careCoordinationResident.getSocialSecurity())
                && patient.getName().getFirstName().equalsIgnoreCase(careCoordinationResident.getFirstName())
                && patient.getName().getLastName().equalsIgnoreCase(careCoordinationResident.getLastName())
                && patient.getDateOfBirth().toGregorianCalendar().getTime().equals(careCoordinationResident.getBirthDate());
    }

    private CareCoordinationResident updatePatientData(Patient patient, CareCoordinationResident careCoordinationResident) {
        boolean update = !patientsBasicDataEqual(patient, careCoordinationResident);
        if (update) {
            careCoordinationResident.setSocialSecurity(patient.getSSN());
            careCoordinationResident.setFirstName(patient.getName().getFirstName());
            careCoordinationResident.setLastName(patient.getName().getLastName());
            careCoordinationResident.setMiddleName(patient.getName().getMiddleName());
            if (!CollectionUtils.isEmpty(careCoordinationResident.getPerson().getNames())) {
                Name name = careCoordinationResident.getPerson().getNames().get(0);
                name.setGiven(patient.getName().getFirstName());
                name.setFamily(patient.getName().getLastName());
            }
            careCoordinationResident.setBirthDate(patient.getDateOfBirth().toGregorianCalendar().getTime());
        }

        if (careCoordinationResident.getGender() == null || !patient.getGender().equalsIgnoreCase(careCoordinationResident.getGender().getCode())) {
            careCoordinationResident.setGender(ccdCodeDao.getGenderCcdCode(Gender.getGenderByCode(patient.getGender())));
            update = true;
        }

        if (careCoordinationResident.getMaritalStatus() == null && StringUtils.isNotBlank(patient.getMaritalStatus())) {
            careCoordinationResident.setMaritalStatus(ccdCodeDao.getMaritalStatus(patient.getMaritalStatus()));
            update = true;
        } else if (StringUtils.isNotBlank(patient.getMaritalStatus()) && careCoordinationResident.getMaritalStatus() != null && !patient.getMaritalStatus().equalsIgnoreCase(careCoordinationResident.getMaritalStatus().getDisplayName())) {
            careCoordinationResident.setMaritalStatus(ccdCodeDao.getMaritalStatus(patient.getMaritalStatus()));
            update = true;
        } else if (careCoordinationResident.getMaritalStatus() != null && StringUtils.isBlank(patient.getMaritalStatus())) {
            careCoordinationResident.setMaritalStatus(null);
            update = true;
        }

        if (CollectionUtils.isEmpty(careCoordinationResident.getPerson().getAddresses()) && !CollectionUtils.isEmpty(patient.getAddress())) {
            careCoordinationResident.getPerson().setAddresses(createAddresses(careCoordinationResident.getDatabase(), careCoordinationResident.getPerson(), patient.getAddress()));
            update = true;
        } else if (!CollectionUtils.isEmpty(careCoordinationResident.getPerson().getAddresses()) && CollectionUtils.isEmpty(patient.getAddress())) {
            careCoordinationResidentDao.deletePersonAddresses(careCoordinationResident.getPerson().getId());
            careCoordinationResident.getPerson().setAddresses(new ArrayList<PersonAddress>());
            update = true;
        } else if (careCoordinationResident.getPerson().getAddresses().size() != patient.getAddress().size()) {
            careCoordinationResidentDao.deletePersonAddresses(careCoordinationResident.getPerson().getId());
            careCoordinationResident.getPerson().setAddresses(createAddresses(careCoordinationResident.getDatabase(), careCoordinationResident.getPerson(), patient.getAddress()));
            update = true;
        } else {
            boolean updateAddressesList = false;
            for (com.scnsoft.eldermark.schema.Address address : patient.getAddress()) {
                boolean addressEqual = false;
                for (Address currentAddress : careCoordinationResident.getPerson().getAddresses()) {
                    if (address.getCity().equalsIgnoreCase(currentAddress.getCity()) && address.getZip().equalsIgnoreCase(currentAddress.getPostalCode())
                            && address.getState().equalsIgnoreCase(currentAddress.getState()) && address.getStreet().equalsIgnoreCase(currentAddress.getStreetAddress())) {
                        addressEqual = true;
                        break;
                    }
                }
                if (!addressEqual) {
                    updateAddressesList = true;
                    break;
                }
            }
            if (updateAddressesList) {
                careCoordinationResidentDao.deletePersonAddresses(careCoordinationResident.getPerson().getId());
                careCoordinationResident.getPerson().setAddresses(createAddresses(careCoordinationResident.getDatabase(), careCoordinationResident.getPerson(), patient.getAddress()));
                update = true;
            }
        }

        if (update) {
            careCoordinationResident = careCoordinationResidentDao.merge(careCoordinationResident);
            careCoordinationResidentDao.flush();
        }
        return careCoordinationResident;
    }

    private List<PersonAddress> createAddresses(Database database, Person person, List<com.scnsoft.eldermark.schema.Address> addresses) {
        List<PersonAddress> result = new ArrayList<PersonAddress>();
        for (com.scnsoft.eldermark.schema.Address address : addresses) {
            result.add(addressService.createPersonAddress(database, person, address));
        }
        return result;
    }

    private void updateLegacyId(String patientId, List<CareCoordinationResident> foundResident) {
        if (StringUtils.isNotBlank(patientId) && !CollectionUtils.isEmpty(foundResident)) {
            CareCoordinationResident residentToUpdateLegacyId = foundResident.get(0);
            if (!patientId.equalsIgnoreCase(residentToUpdateLegacyId.getLegacyId())) {
                residentToUpdateLegacyId.setLegacyId(patientId);
                careCoordinationResidentDao.merge(residentToUpdateLegacyId);
                careCoordinationResidentDao.flush();
            }
        }
    }

    private Long getIdByPatientIdInOrganization(String patientId, Long databaseId) {
        Long idFoundByPatientId = null;
        if (StringUtils.isNotBlank(patientId)) {
            Resident resident = residentDao.getResident(databaseId, patientId);
            if (resident != null) {
                idFoundByPatientId = resident.getId();
            }
        }
        return idFoundByPatientId;
    }

    private Long getIdByPatientIdInCommunity(String patientId, Long communityId) {
        Long idFoundByPatientId = null;
        if (StringUtils.isNotBlank(patientId)) {
            Resident resident = residentDao.getResidentInCommunity(communityId, patientId);
            if (resident != null) {
                idFoundByPatientId = resident.getId();
            }
        }
        return idFoundByPatientId;
    }

    private boolean checkExistInList(List<CareCoordinationResident> residents, List<CareCoordinationResident> matchedResidents) {
        for (CareCoordinationResident matchedResident : matchedResidents) {
            for (CareCoordinationResident resident : residents) {
                if (resident.getId().equals(matchedResident.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public CareCoordinationResident createOrUpdateResident(Long organizationId, Long id, PatientDto patient) {
        final Organization organization = organizationDao.getOrganization(organizationId);
        logger.info("[CareCoordinationResidentServiceImpl] Organization id : {}, organization is {}", organizationId, organization);
        return createOrUpdateCareCoordinationResident(patient, organization, id, true);
    }

    @Override
    public Boolean toggleResidentActivation(Long organizationId, Long id) {
        CareCoordinationResident resident = careCoordinationResidentDao.get(id);
        if (resident == null) throw new BusinessAccessDeniedException("Resident not found");
        if (!isResidentEditable(resident)) {
            throw new BusinessAccessDeniedException("User has not enough privileges to edit this patient");
        }
        resident.setActive(!resident.getActive());
        careCoordinationResidentDao.merge(resident);
        return resident.getActive();
    }


    private CareCoordinationResident createOrUpdateCareCoordinationResident(Patient patient, Organization organization,
                                                                            Long id, boolean createdManually, boolean existsInOrganization) {
        return createOrUpdateCareCoordinationResident(convert(patient), organization, id, createdManually, existsInOrganization);
    }


    private CareCoordinationResident createOrUpdateCareCoordinationResident(PatientDto patient, Organization organization, Long id, boolean createdManually) {
        return createOrUpdateCareCoordinationResident(convert(patient), organization, id, createdManually, null);
    }

    private CareCoordinationResident createOrUpdateCareCoordinationResident(PatientDtoExtended patient, Organization organization,
                                                                            Long id, boolean createdManually, Boolean existsInOrganization) {
        boolean create = (id == null) || (id == 0L);

        //Fill In Resident Table Data

        final CareCoordinationResident resident;
        if (create) {
            if (careCoordinationResidentDao.checkExistResidentByIdentityFields(organization.getId(), patient.getSsn(), patient.getBirthDate(), patient.getLastName(), patient.getFirstName())) {
                throw new BusinessException("Resident with such SSN, Date Of Birth, LastName and FirstName already exists in the system");
            }
//            if (createdManually) {
//                if (careCoordinationResidentDao.getResidentByIdentityFields(organization.getId(), patient.getSsn(), patient.getBirthDate(), patient.getLastName(), patient.getFirstName()) != null) {
//                }
////                MatchResult matchResult = residentMatcherService.findMatchedPatients(patient, false);
////                    if (!matchResult.getMatchResultType().equals( MatchResult.MatchResultType.NO_MATCH)){
////                        throw new BusinessException("Resident already exists in the system");
////                    }
//            }

            // validate unique filds for new resident
            if (StringUtils.isNotEmpty(patient.getMemberNumber())
                    && careCoordinationResidentDao.getResidentIdWithMemberId(patient.getMemberNumber(), organization.getDatabaseId(), organization.getId()) != null) {
                throw new NotUniqueValueException("Member ID should be unique within the community");
            }
            if (StringUtils.isNotEmpty(patient.getMedicareNumber())
                    && careCoordinationResidentDao.getResidentIdWithMedicareNumber(patient.getMedicareNumber(), organization.getDatabaseId(), organization.getId()) != null) {
                throw new NotUniqueValueException("Medicare Number should be unique within the community");
            }
            if (StringUtils.isNotEmpty(patient.getMedicaidNumber())
                    && careCoordinationResidentDao.getResidentIdWithMedicaidNumber(patient.getMedicaidNumber(), organization.getDatabaseId(), organization.getId()) != null) {
                throw new NotUniqueValueException("Medicaid Number should be unique within the community");
            }

            resident = new CareCoordinationResident();
            logger.info("[CareCoordinationResidentServiceImpl] Organization is {} , Database id {} , Database {}", organization, organization.getDatabaseId(), organization.getDatabase());
            resident.setDatabase(organization.getDatabase());
            resident.setDatabaseId(organization.getDatabaseId());
            if (BooleanUtils.isFalse(existsInOrganization) && StringUtils.isNotBlank(patient.getLegacyId())) {
                resident.setLegacyId(patient.getLegacyId());
            } else {
                CareCoordinationConstants.setLegacyId(resident);
            }
            resident.setFacility(organization);
            if (createdManually) {
                resident.setLegacyTable(CareCoordinationConstants.CCN_MANUAL_LEGACY_TABLE);
                resident.setCreatedById(SecurityUtils.getAuthenticatedUser().getEmployeeId());
            } else {
                resident.setLegacyTable(CareCoordinationConstants.RBA_PERSON_LEGACY_TABLE);
            }
        } else {
            resident = careCoordinationResidentDao.get(id);
            if (resident == null) throw new BusinessAccessDeniedException("Resident not found");
            if (createdManually) {
                if (!isResidentEditable(resident))
                    throw new BusinessAccessDeniedException("User has not enough privileges to edit this patient");
                CareCoordinationResident sameResident = careCoordinationResidentDao.getResidentByIdentityFields(resident.getFacility()!= null ? resident.getFacility().getId() : null, patient.getSsn(), patient.getBirthDate(), patient.getLastName(), patient.getFirstName());
                logger.info("[CareCoordinationResidentServiceImpl] Creating/Updating a resident with id:{}, ssn:{}, community:{}, birthDate:{}, lastName:{}, firstName:{}",
                        patient.getId(), patient.getSsn(), resident.getFacility() != null ? resident.getFacility().getId() : null, patient.getBirthDate(), patient.getLastName(), patient.getFirstName());
                if ((sameResident != null) && (!sameResident.getId().equals(resident.getId()))) {
                    logger.info("[CareCoordinationResidentServiceImpl] Found resident with the same identity fields. Exception will be thrown.  Id:{}, ssn:{}, community:{}, birthDate:{}, lastName:{}, firstName:{}",
                            sameResident.getId(), sameResident.getSocialSecurity(), resident.getFacility() != null ? resident.getFacility().getId() : null, sameResident.getBirthDate(), sameResident.getLastName(), sameResident.getFirstName());
                    throw new BusinessException("Resident with such SSN, Date Of Birth, LastName and FirstName already exists in the Community");
                }
            }

            // validate unique fields for residents if only they were changed after editing
            Long foundId;
            if (StringUtils.isNotEmpty(patient.getMemberNumber())) {
                foundId = careCoordinationResidentDao.getResidentIdWithMemberId(patient.getMemberNumber(), resident.getFacility().getDatabaseId(), resident.getFacility().getId());
                if (foundId != null && !foundId.equals(id)) {
                    throw new NotUniqueValueException("Member ID should be unique within the community");
                }
            }
            if (StringUtils.isNotEmpty(patient.getMedicareNumber())) {
                foundId = careCoordinationResidentDao.getResidentIdWithMedicareNumber(patient.getMedicareNumber(), resident.getFacility().getDatabaseId(), resident.getFacility().getId());
                if (foundId != null && !foundId.equals(id)) {
                    throw new NotUniqueValueException("Medicare Number should be unique within the community");
                }
            }
            if (StringUtils.isNotEmpty(patient.getMedicaidNumber())) {
                foundId = careCoordinationResidentDao.getResidentIdWithMedicaidNumber(patient.getMedicaidNumber(), resident.getFacility().getDatabaseId(), resident.getFacility().getId());
                if (foundId != null && !foundId.equals(id)) {
                    throw new NotUniqueValueException("Medicaid Number should be unique within the community");
                }
            }
        }
        String ssn = patient.getSsn();
        if (ssn != null) {
            ssn = ssn.replaceAll("-", "");
        }
        resident.setSocialSecurity(ssn);
        if (ssn != null) {
            resident.setSsnLastFourDigits(ssn.substring(ssn.length() - 4));
        }
        resident.setLastName(patient.getLastName());
        resident.setFirstName(patient.getFirstName());
        resident.setMiddleName(patient.getMiddleName());
        if (StringUtils.isNotBlank(patient.getGender())) {
            resident.setGender(ccdCodeDao.getGenderCcdCode(Gender.getGenderByCode(patient.getGender())));
        }
        if (StringUtils.isNotBlank(patient.getMaritalStatus())) {
            resident.setMaritalStatus(ccdCodeDao.getMaritalStatus(patient.getMaritalStatus()));
        }
        if (patient.getBirthDate() != null) {
            resident.setBirthDate(patient.getBirthDate());
        }

        if (patient.getInsuranceId() != null) {
            resident.setInNetworkInsurance(inNetworkInsuranceDao.getOne(patient.getInsuranceId()));
        }
        resident.setInsurancePlanName(patient.getInsurancePlan());
        resident.setGroupNumber(patient.getGroupNumber());
        resident.setMemberNumber(patient.getMemberNumber());
        resident.setMedicareNumber(patient.getMedicareNumber());
        resident.setMedicaidNumber(patient.getMedicaidNumber());
        resident.setRetained(patient.getRetained());
        //resident.setPrimaryCarePhysician(patient.getPrimaryCarePhysician());
        resident.setIntakeDate(patient.getIntakeDate());
        resident.setReferralSource(patient.getReferralSource());
        resident.setCurrentPharmacyName(patient.getCurrentPharmacyName());

        //Fill In Person table Data

        final Person person;
        if (create) {
            person = new Person();
            CareCoordinationConstants.setLegacyId(person);
            if (createdManually) {
                person.setLegacyTable(CareCoordinationConstants.CCN_MANUAL_LEGACY_TABLE);
            } else {
                person.setLegacyTable(CareCoordinationConstants.RBA_PERSON_LEGACY_TABLE);
            }
            person.setDatabase(organization.getDatabase());
            resident.setPerson(person);
        } else {
            person = resident.getPerson();
        }

        //Fill In Name table Data

        Name name = null;
        if (!create) {
            //TRY TO FIND NAME WITH USE_NAME=L
            List<Name> names = person.getNames();
            if (names != null) {
                for (Name item : names) {
                    if ("L".equals(item.getNameUse())) {
                        name = item;
                        break;
                    }
                }
            }
        }
        boolean createNewName = name == null;
        if (createNewName) {
            name = new Name();
            name.setDatabase(organization.getDatabase());
            name.setPerson(person);

            CareCoordinationConstants.setLegacyId(name);
            name.setLegacyTable(CareCoordinationConstants.RBA_NAME_LEGACY_TABLE);
            name.setNameUse("L");
            if (person.getNames() == null) person.setNames(new ArrayList<Name>());
            person.getNames().add(name);
        }

        name.setFamily(patient.getLastName());
        name.setFamilyNormalized(CareCoordinationUtils.normalizeName(patient.getLastName()));

        name.setGiven(patient.getFirstName());
        name.setGivenNormalized(CareCoordinationUtils.normalizeName(patient.getFirstName()));

//        name.setMiddle(patient.getMiddleName());
//        name.setMiddleNormalized(normalizeName(patient.getMiddleName()));

        //Fill In Address table Data
        boolean addressCreated = false;
        if (!CollectionUtils.isEmpty(patient.getAddressList())) {
            if (person.getAddresses() == null) person.setAddresses(new ArrayList<PersonAddress>());
            int i = 0;
            for (final AddressDto address : patient.getAddressList()) {
                boolean createAddress = (resident.getPerson().getAddresses().size() <= i);
                if (createAddress) {
                    final PersonAddress personAddress = addressService.createPersonAddress(organization.getDatabase(), person, address);
                    CareCoordinationConstants.setLegacyIdFromParent(personAddress, resident.getPerson());
                    person.getAddresses().add(personAddress);
                    addressCreated = true;
                } else {
                    PersonAddress personAddress = resident.getPerson().getAddresses().get(i);
                    addressService.updatePersonAddress(personAddress, address);
                }
                i++;
            }
        }

        //Fill in telecom data
        boolean telecomCreated = false;
        if (person.getTelecoms() == null) {
            person.setTelecoms(new ArrayList<PersonTelecom>());
        }
        PersonTelecom existingPhone = null;
        for (PersonTelecom telecom : person.getTelecoms()) {
            if (PersonTelecomCode.HP.name().equals(telecom.getUseCode())) {
                existingPhone = telecom;
                break;
            }
        }
        if (existingPhone == null) {
            if (StringUtils.isNotEmpty(patient.getPhone())) {
                createPersonTelecom(person, PersonTelecomCode.HP, patient.getPhone());
                telecomCreated = true;
            }
        } else {
            existingPhone.setValue(patient.getPhone());
        }

        PersonTelecom existingMail = null;
        for (PersonTelecom telecom : person.getTelecoms()) {
            if (PersonTelecomCode.EMAIL.name().equals(telecom.getUseCode())) {
                existingMail = telecom;
                break;
            }
        }
        if (existingMail == null) {
            if (StringUtils.isNotEmpty(patient.getEmail())) {
                createPersonTelecom(person, PersonTelecomCode.EMAIL, patient.getEmail());
                telecomCreated = true;
            }
        } else {
            existingMail.setValue(patient.getEmail());
        }

        if (StringUtils.isNotEmpty(patient.getCellPhone())) {
            PersonTelecom existingCellPhone = null;
            for (PersonTelecom telecom : person.getTelecoms()) {
                if (PersonTelecomCode.MC.name().equals(telecom.getUseCode())) {
                    existingCellPhone = telecom;
                    break;
                }
            }
            if (existingCellPhone == null) {
                createPersonTelecom(person, PersonTelecomCode.MC, patient.getCellPhone());
                telecomCreated = true;
            } else {
                existingCellPhone.setValue(patient.getCellPhone());
            }
        }

        //TODO 2 devices supported for phase 1 dose health
        if (StringUtils.isNotEmpty(patient.getDeviceID()) || StringUtils.isNotEmpty(patient.getDeviceIDSecondary())) {
            if (StringUtils.isNotEmpty(patient.getDeviceID()) && StringUtils.isNotEmpty(patient.getDeviceIDSecondary()) &&
                    patient.getDeviceID().equals(patient.getDeviceIDSecondary())) {
                throw new BusinessException("Patient can't have equal device id ans secondary device id: " + patient.getDeviceID());
            }
            List<ResidentDevice> devices = resident.getDevices();
            List<ResidentDevice> usedDevices = new ArrayList<>();
            if (create) {
                usedDevices.add(residentDeviceService.findByDeviceIdAndFacilityId(patient.getDeviceID(), resident.getFacility().getId()));
                usedDevices.add(residentDeviceService.findByDeviceIdAndFacilityId(patient.getDeviceIDSecondary(), resident.getFacility().getId()));
            } else {
                usedDevices.add(residentDeviceService.findIfUsedByAnotherResidentAndFacility(patient.getDeviceID(), patient.getId(), resident.getFacility().getId()));
                usedDevices.add(residentDeviceService.findIfUsedByAnotherResidentAndFacility(patient.getDeviceIDSecondary(), patient.getId(), resident.getFacility().getId()));
            }
            for (ResidentDevice device : usedDevices) {
                if (device != null) {
                    residentDeviceService.delete(device.getId());
                }
            }

            if (devices == null) {
                devices = new ArrayList<>();
                resident.setDevices(devices);
            }
            List<ResidentDevice> newDevices = new ArrayList<>();
            if (StringUtils.isNotEmpty(patient.getDeviceID())) {
                ResidentDevice residentDevice = createResidentDevice(patient.getDeviceID(), resident);
                newDevices.add(residentDevice);
            }
            if (StringUtils.isNotEmpty(patient.getDeviceIDSecondary())) {
                ResidentDevice residentDevice = createResidentDevice(patient.getDeviceIDSecondary(), resident);
                newDevices.add(residentDevice);
            }
            updateExistingDeviceList(devices, newDevices);
        } else {
            if (CollectionUtils.isNotEmpty(resident.getDevices())) {
                resident.getDevices().clear();
            }
        }

//        resident.setLastUpdated(new Date());
        //Create or Update entities. Update legacy ids and MPI
        if (create) {
            careCoordinationResidentDao.create(resident);
            mpiService.createMPI(resident.getId(), null);
            // update legacy ids
            CareCoordinationConstants.setLegacyId(resident);
            CareCoordinationConstants.setLegacyId(resident.getPerson());
        } else {
            careCoordinationResidentDao.merge(resident);
        }

        if (createNewName) {
            for (final Name n : resident.getPerson().getNames()) {
                CareCoordinationConstants.setLegacyId(n);
            }
        }
        if (addressCreated) {
            for (final PersonAddress personAddress : resident.getPerson().getAddresses()) {
                //CareCoordinationConstants.setLegacyId(personAddress);
                CareCoordinationConstants.setLegacyIdFromParent(personAddress, resident.getPerson());
            }
        }

        if (telecomCreated) {
            for (final PersonTelecom personTelecom : resident.getPerson().getTelecoms()) {
                CareCoordinationConstants.setLegacyIdFromParent(personTelecom, resident.getPerson());
            }
        }

        return careCoordinationResidentDao.merge(resident);
    }

    /**
     * method to update collection of devices with new devices without removing the same items
     *
     * @param devices
     * @param newDevices
     */
    private void updateExistingDeviceList(List<ResidentDevice> devices, List<ResidentDevice> newDevices) {
        List<ResidentDevice> devicesToDelete = new ArrayList<>();
        for (ResidentDevice residentDevice : devices) {
            if (!deviceExistsInList(residentDevice.getDeviceId(), newDevices)) {
                devicesToDelete.add(residentDevice);
            }
        }
        devices.removeAll(devicesToDelete);
        for (ResidentDevice newDevice : newDevices) {
            if (!deviceExistsInList(newDevice.getDeviceId(), devices)) {
                devices.add(newDevice);
            }
        }
    }

    private boolean deviceExistsInList(String deviceId, List<ResidentDevice> devices) {
        boolean result = false;
        for (ResidentDevice residentDevice : devices) {
            if (residentDevice.getDeviceId().equals(deviceId)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private ResidentDevice createResidentDevice(String deviceId, CareCoordinationResident resident) {
        ResidentDevice residentDevice = new ResidentDevice();
        residentDevice.setDeviceId(deviceId);
        residentDevice.setResident(resident);
        return residentDevice;
    }


    private PersonTelecom createPersonTelecom(final Person person, final PersonTelecomCode code, final String value) {
        final PersonTelecom telecom = new PersonTelecom();
        telecom.setPerson(person);
        CareCoordinationConstants.setLegacyIdFromParent(telecom, person);
        telecom.setLegacyTable(CareCoordinationConstants.RBA_PERSON_TELECOM_LEGACY_TABLE);
        telecom.setSyncQualifier(code.getCode());

        telecom.setUseCode(code.name());
        telecom.setValue(value);
        telecom.setDatabase(person.getDatabase());

        if (person.getTelecoms() == null) person.setTelecoms(new ArrayList<PersonTelecom>());
        person.getTelecoms().add(telecom);
        return telecom;
    }

    private PatientDtoExtended convert(Patient src) {
        PatientDtoExtended dest = new PatientDtoExtended();
        dest.setLastName(src.getName() != null ? src.getName().getLastName() : null);
        dest.setFirstName(src.getName() != null ? src.getName().getFirstName() : null);
        dest.setMiddleName(src.getName() != null ? src.getName().getMiddleName() : null);
        dest.setBirthDate(src.getDateOfBirth() != null ? src.getDateOfBirth().toGregorianCalendar().getTime() : null);
        dest.setGender(src.getGender());
        dest.setSsn(src.getSSN());
        dest.setMaritalStatus(src.getMaritalStatus());
        dest.setEmail(null);
        dest.setPhone(null);
        dest.setLegacyId(src.getPatientId());
        if (src.getAddress() != null) {
            List<AddressDto> destAddressList = new ArrayList<AddressDto>();
            for (com.scnsoft.eldermark.schema.Address srcAddress : src.getAddress()) {
                AddressDto destAddress = new AddressDto();
                destAddress.setZip(srcAddress.getZip());
                destAddress.setCity(srcAddress.getCity());
                destAddress.setStreet(srcAddress.getStreet());
                if (srcAddress.getState() != null) {
                    // FIXME NPE if stateService returns null (add <state> value check to validation)
                    destAddress.setState(CareCoordinationUtils.createKeyValueDto(stateService.findByAbbrOrFullName(srcAddress.getState())));
                }
                destAddressList.add(destAddress);
            }
            dest.setAddressList(destAddressList);
        }
        return dest;
    }

    private PatientDtoExtended convert(PatientDto dto) {
        PatientDtoExtended patient = new PatientDtoExtended(dto);
        return patient;
    }

    @Override
    public boolean isResidentEditable(Set<GrantedAuthority> authorities, Long employeeCommunityId, CareCoordinationResident resident) {
        if (!CareCoordinationConstants.CCN_MANUAL_LEGACY_TABLE.equals(resident.getLegacyTable()) && !SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR))
            return false;
        ExchangeUserDetails user = SecurityUtils.getAuthenticatedUser();
        if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            return true;
        } else if (SecurityUtils.isAffiliatedView()) {
            return false;
        } else if (SecurityUtils.hasAnyRole(authorities, CareTeamRoleCode.CASE_MANAGER, CareTeamRoleCode.CARE_COORDINATOR, CareTeamRoleCode.ADMINISTRATOR)) {
            return (resident.getDatabaseId() == user.getCurrentDatabaseId().longValue());
        } else if (SecurityUtils.hasRole(authorities, CareTeamRoleCode.COMMUNITY_ADMINISTRATOR)) {
            return (resident.getFacility().getId() == employeeCommunityId.longValue());
        } else return false;
    }

    @Override
    public boolean isResidentEditable(CareCoordinationResident resident) {
        boolean editable = false;
        Set<Long> employeeIds = getLoggedEmployeeIdsAvailableForPatient(resident.getId());
        if (!CollectionUtils.isEmpty(employeeIds)) {
            for (Long employeeId : employeeIds) {
                Set<GrantedAuthority> currentEmployeeAuthorities = SecurityUtils.getAuthenticatedUser().getEmployeeAuthoritiesMap().get(employeeId);
                Long employeeCommunityId = SecurityUtils.getAuthenticatedUser().getLinkedEmployeeById(employeeId).getCommunityId();
                editable = editable || isResidentEditable(currentEmployeeAuthorities, employeeCommunityId, resident);
            }
        }
        return editable;
    }

    @Override
    public void addToIndex() {
        logger.info("CareCoordinationResident indexing job started!");
        if (careCoordinationResidentDao.isFirstTimeIndexed()) {
            logger.info("First time create full reindex!");
            careCoordinationResidentDao.createIndex();
        } else {
            List<CareCoordinationResident> residents = careCoordinationResidentDao.getLastUpdatedResidents();
            for (CareCoordinationResident resident : residents) {
                careCoordinationResidentDao.addToIndex(resident);
                System.out.println("Added index for resident " + resident.getId() + ", " + resident.getFirstName() + " " + resident.getLastName() + ", " + resident.getSsnLastFourDigits());
            }
        }
        List<String> deletedResidentRecords = careCoordinationResidentDao.getDeletedResidentRecords();
        for (String deletedRecord : deletedResidentRecords) {
            Long residentId = getResidentIdFromDeletedRecord(deletedRecord);
            if (residentId != null) {
                careCoordinationResidentDao.deleteIndex(residentId);
                logger.info("Deleted index for Resident: " + deletedRecord);
            } else {
                logger.error("Error parse record in DataSyncDeletedDataLog: " + deletedRecord);
            }
        }
        careCoordinationResidentDao.updateMpiLog();
    }

    private Long getResidentIdFromDeletedRecord(String deletedRecord) {
        deletedRecord = deletedRecord.substring(deletedRecord.indexOf("{") + 1, deletedRecord.indexOf("}"));
        String[] records = deletedRecord.split(",");
        for (String record : records) {
            String[] nameValue = record.split("=");
            if (nameValue[0].trim().equals("id")) {
                String idStr = nameValue[1].replace("\'", "");
                return Long.parseLong(idStr);
            }
        }
        return null;
    }

    @Override
    public CareCoordinationResident get(Long id) {
        return careCoordinationResidentDao.get(id);
    }

    public List<PatientListItemDto> getMergedResidents(long patientId, Boolean showDeactivated) {
        List<Long> mergedIds = mpiService.listMergedResidents(patientId);

//        List<CareCoordinationResident> mergedResidents = new ArrayList<CareCoordinationResident>();
//        Long databaseId = SecurityUtils.getAuthenticatedUser().getDatabaseId();
//        for(Long mergedResidentId : mergedIds) {
//            CareCoordinationResident mergedResident = careCoordinationResidentDao.get(mergedResidentId);
//            if(mergedResident != null && mergedResident.getDatabase().getId().equals(databaseId)) {
//                mergedResidents.add(mergedResident);
//            }
//        }
        Long databaseId = SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId();
        return careCoordinationResidentDao.getMergedResidents(mergedIds, databaseId, showDeactivated);

//        return mergedResidents;
    }

    public List<Long> getMergedResidentIds(List<Long> patientIds) {
        List<Long> mergedIds = new ArrayList<Long>();
        for (Long patientId : patientIds) {                          //TODO optimize: make possibility to do this in 1 request
            mergedIds.addAll(mpiService.listMergedResidents(patientId));
        }
        if (CollectionUtils.isEmpty(mergedIds)) {
            return mergedIds;
        }
        Long databaseId = SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId();
        //get resident Ids only from specified database
        return careCoordinationResidentDao.getMergedResidentIds(mergedIds, databaseId);

    }

    public boolean isExistResident(PatientDto patient) {
        return careCoordinationResidentDao.checkExistResidentByIdentityFields(patient.getCommunityId(), patient.getSsn(), patient.getBirthDate(), patient.getLastName(), patient.getFirstName());
    }

    @Override
    public Long getCommunityId(Long patientId) {
        return careCoordinationResidentDao.getCommunityId(patientId);
    }

    @Override
    public Long getCreatedById(Long patientId) {
        return careCoordinationResidentDao.getCreatedById(patientId);
    }

    @Override
    public Set<Long> getLoggedEmployeeIdsAvailableForPatient(final Long patientId) {
        Set<Long> allEmployeeIds = SecurityUtils.getAuthenticatedUser().getEmployeeAndLinkedEmployeeIds();

        return Sets.newHashSet(Iterables.filter(allEmployeeIds, new Predicate<Long>() {
            @Override
            public boolean apply(Long employeeId) {
                List<Long> residentIds = getResidentsIdsForEmployee(employeeId);
                return residentIds.contains(patientId);
            }
        }));
    }

    @Override
    public void checkAddEditCareTeamAccessToPatientOrThrow(Long careTeamMemberId, Long patientId) {
        final ExchangeUserDetails authenticatedUser = SecurityUtils.getAuthenticatedUser();
        final Set<Long> employeeIds = getLoggedEmployeeIdsAvailableForPatient(patientId);
        final boolean hasAccess;
        if (careTeamMemberId == null) {
            final Resident patient = residentDao.get(patientId);
            hasAccess = CareTeamSecurityUtils.canAddCtm(authenticatedUser, employeeIds, patient.getDatabaseId(), patient.getFacility().getId());
        } else {
            final CareTeamMember careTeamMember = careTeamService.getCareTeamMember(careTeamMemberId);
            hasAccess = CareTeamSecurityUtils.canEditNotificationSettings(authenticatedUser, employeeIds, patientId, careTeamMember);
        }
        if (!hasAccess) {
            throw new BusinessAccessDeniedException("User does not have enough privileges for that operation");
        }
    }

    @Override
    public Set<CareTeamRoleDto> getAllowedCareTeamRoles(Long careTeamMemberId, Long patientId) {
        final ExchangeUserDetails authenticatedUser = SecurityUtils.getAuthenticatedUser();
        final Set<Long> employeeIds = getLoggedEmployeeIdsAvailableForPatient(patientId);
        final Collection<CareTeamRoleCode> careTeamRoles;
        if (careTeamMemberId == null) {
            final Resident patient = residentDao.get(patientId);
            careTeamRoles = CareTeamSecurityUtils.getAllowedCareTeamRolesForCreate(authenticatedUser, employeeIds, patient.getDatabaseId(), patient.getFacility().getId());
        } else {
            final CareTeamMember careTeamMember = careTeamService.getCareTeamMember(careTeamMemberId);
            careTeamRoles = CareTeamSecurityUtils.getAllowedCareTeamRolesForEdit(authenticatedUser, employeeIds, patientId, careTeamMember);
        }

        return Sets.newLinkedHashSet(
                Collections2.filter(
                        Collections2.transform(careTeamRoles, careTeamRoleService.toDto()),
                        Predicates.notNull()
                )
        );
    }

    @Override
    public Long getResidentsCountForCurrentUserAndOrganization() {
        Long databaseId = SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId();
        Set<Long> employeeIds = SecurityUtils.getAuthenticatedUser().getEmployeeAndLinkedEmployeeIds();
        List<Long> communityIds = SecurityUtils.getAuthenticatedUser().getCurrentCommunityIds();
        boolean isAdmin = SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR);
        Pair<Boolean, Set<Long>> adminAndEmployeeIds = getCommunityAdminEmployeeIds(employeeIds);
        Set<Long> employeeCommunityIds = adminAndEmployeeIds.getSecond();
        isAdmin = isAdmin || adminAndEmployeeIds.getFirst();
        Long result = careCoordinationResidentDao.getResidentsForEmployeeCount(employeeIds, null, databaseId, communityIds, isAdmin, employeeCommunityIds);
        return result;
    }

    @Override
    public List<AdmittanceHistory> getAdmittanceHistoryForPatientInCommunity(Long patientId, Long communityId) {
        return admittanceHistoryDao.getByResident_IdAndOrganizationId(patientId, communityId);
    }
}
