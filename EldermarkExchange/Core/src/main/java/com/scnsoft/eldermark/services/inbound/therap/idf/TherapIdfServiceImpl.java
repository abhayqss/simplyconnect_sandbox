package com.scnsoft.eldermark.services.inbound.therap.idf;

import com.google.common.base.Optional;
import com.scnsoft.eldermark.dao.CcdCodeDao;
import com.scnsoft.eldermark.dao.JpaAdmittanceHistoryDao;
import com.scnsoft.eldermark.dao.TherapUnknownNetworkPlanDao;
import com.scnsoft.eldermark.dao.marketplace.InsurancePlanDao;
import com.scnsoft.eldermark.dao.phr.InNetworkInsuranceDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.inbound.therap.csv.TherapIdfCSV;
import com.scnsoft.eldermark.entity.marketplace.InsurancePlan;
import com.scnsoft.eldermark.entity.phr.InNetworkInsurance;
import com.scnsoft.eldermark.services.OrganizationService;
import com.scnsoft.eldermark.services.ResidentService;
import com.scnsoft.eldermark.services.carecoordination.CareCoordinationConstants;
import com.scnsoft.eldermark.services.exceptions.TherapBusinessException;
import com.scnsoft.eldermark.services.inbound.therap.TherapInboundFilesServiceRunCondition;
import com.scnsoft.eldermark.shared.Gender;
import com.scnsoft.eldermark.shared.carecoordination.utils.CareCoordinationUtils;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.ExchangeStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Service
@Conditional(TherapInboundFilesServiceRunCondition.class)
public class TherapIdfServiceImpl implements TherapIdfService {

    private static final Logger logger = LoggerFactory.getLogger(TherapIdfServiceImpl.class);
    private static final String NAME_USE_CODE = "L";
    private static final String THERAP_LEGACY_TABLE_PREFIX = "THERAP_IDF_IMPORT__";
    private static final int RESIDENT_LEGACY_TABLE_LENGTH = 100;

    private final ResidentService residentService;
    private final CcdCodeDao ccdCodeDao;
    private final OrganizationService organizationService;
    private final InsurancePlanDao insurancePlanDao;
    private final InNetworkInsuranceDao inNetworkInsuranceDao;
    private final TherapUnknownNetworkPlanDao therapUnknownNetworkPlanDao;
    private final JpaAdmittanceHistoryDao jpaAdmittanceHistoryDao;


    @Autowired
    public TherapIdfServiceImpl(ResidentService residentService, CcdCodeDao ccdCodeDao,
                                OrganizationService organizationService, InsurancePlanDao insurancePlanDao,
                                InNetworkInsuranceDao inNetworkInsuranceDao, TherapUnknownNetworkPlanDao therapUnknownNetworkPlanDao,
                                JpaAdmittanceHistoryDao jpaAdmittanceHistoryDao) {
        this.residentService = residentService;
        this.ccdCodeDao = ccdCodeDao;
        this.organizationService = organizationService;
        this.insurancePlanDao = insurancePlanDao;
        this.inNetworkInsuranceDao = inNetworkInsuranceDao;
        this.therapUnknownNetworkPlanDao = therapUnknownNetworkPlanDao;
        this.jpaAdmittanceHistoryDao = jpaAdmittanceHistoryDao;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW) //todo require new
    public Pair<Resident, Boolean> createOrUpdateResident(TherapIdfCSV therapIdf, Long idfCommunityId) {
        validateInput(therapIdf, idfCommunityId);
        Organization community = organizationService.getOrganization(idfCommunityId);
        validateProviderCode(therapIdf, community);

        final Optional<Resident> foundResident = findResident(therapIdf, community);
        if (foundResident.isPresent()) {
            Resident resident = updateResident(therapIdf, foundResident.get());
            return new Pair<>(resident, true);
        } else {
            Resident resident = createResident(therapIdf, community);
            return new Pair<>(resident, false);
        }
    }

    private void validateProviderCode(TherapIdfCSV therapIdf, Organization community) {
        if (!therapIdf.getProviderCode().equalsIgnoreCase(community.getDatabase().getOid())) {
            throw new TherapBusinessException("Organization with oid [" + therapIdf.getProviderCode() + "] is not" +
                    "related with Community [" + community.getId() + "]");
        }
    }

    private Optional<Resident> findResident(TherapIdfCSV idfCSV, Organization community) {
        if (StringUtils.isEmpty(idfCSV.getIdNumber())) {
            return findResidentByIdentityFieldsInCommunity(idfCSV, community);
        }

        final Optional<Resident> dbResident = residentService.getResidentByIdentityFields(community.getDatabase().getId(), community.getId(), idfCSV.getIdNumber());
        if (!dbResident.isPresent()) {
            return findResidentByIdentityFieldsInCommunity(idfCSV, community);
        }
        return dbResident;
    }

    private Resident createResident(TherapIdfCSV therapIdfCSV, Organization community) {
        Resident resident = new Resident();
        resident.setLegacyTable(THERAP_LEGACY_TABLE_PREFIX + resolveLegacyTablePostfix(community));

        resident.setDatabaseId(community.getDatabase().getId());
        resident.setDatabase(community.getDatabase());
        resident.setFacility(community);

        fillResident(resident, therapIdfCSV);

        resident = residentService.createResident(resident);
        resident = residentService.updateLegacyIds(resident);
        resident = updateAdmittanceHistory(resident, therapIdfCSV.getAdmissionDate());

        return resident;
    }

    private Resident updateResident(TherapIdfCSV therapIdfCSV, Resident resident) {
        fillResident(resident, therapIdfCSV);

        resident = residentService.updateResident(resident);
        resident = residentService.updateLegacyIds(resident);
        resident = updateAdmittanceHistory(resident, therapIdfCSV.getAdmissionDate());
        return resident;
    }

    private String resolveLegacyTablePostfix(Organization community) {
        String value;
        if (StringUtils.isNotEmpty(community.getOid())) {
            value = community.getOid();
        } else {
            value = community.getName();
        }
        value = value.replaceAll("[^\\w-]+", "_");
        return value.substring(0,
                Math.min(RESIDENT_LEGACY_TABLE_LENGTH - THERAP_LEGACY_TABLE_PREFIX.length(), value.length()));
    }

    private void validateInput(TherapIdfCSV therapIdfCSV, Long idfCommunityId) {
        if (StringUtils.isAnyEmpty(therapIdfCSV.getFirstName(), therapIdfCSV.getLastName(), therapIdfCSV.getSsn())
                || therapIdfCSV.getBirthDate() == null) {
            throw new TherapBusinessException(
                    "Can't process IDF record because any of the FirstName, LastName, SSN or DateOfBirth is missing.");
        }

        if (StringUtils.isEmpty(therapIdfCSV.getProviderCode())) {
            throw new TherapBusinessException("Can't process IDF record because provider code is missing.");
        }

        if (idfCommunityId == null) {
            throw new TherapBusinessException("Can't process IDF record because program enrollment by idfFormId is missing.");
        }
    }

    private Optional<Resident> findResidentByIdentityFieldsInCommunity(TherapIdfCSV idfCSV, Organization community) {
        Resident dbResident = residentService.getResidentByIdentityFields(
                community.getId(),
                idfCSV.getSsn(),
                idfCSV.getBirthDate().getTime(),
                idfCSV.getLastName(),
                idfCSV.getFirstName()
        );
        return Optional.fromNullable(dbResident);
    }

    private void fillResident(Resident resident, TherapIdfCSV therapIdfCSV) throws TherapBusinessException {
        Person person = createOrUpdatePerson(therapIdfCSV, resident);
        resident.setPerson(person);

        resident.setFirstName(therapIdfCSV.getFirstName());
        resident.setLastName(therapIdfCSV.getLastName());
        resident.setMiddleName(therapIdfCSV.getMiddleName());

        if (StringUtils.isEmpty(resident.getLegacyId())) {
            if (StringUtils.isNotEmpty(therapIdfCSV.getIdNumber())) {
                resident.setLegacyId(therapIdfCSV.getIdNumber());
            } else {
                CareCoordinationConstants.setLegacyId(resident);
            }
        } else if (StringUtils.isNotEmpty(therapIdfCSV.getIdNumber()) && !StringUtils.equalsIgnoreCase(resident.getLegacyId(), therapIdfCSV.getIdNumber())) {
            logger.info("Updating legacy id of resident [{}], [{}] -> [{}]", resident.getId(), resident.getLegacyId(), therapIdfCSV.getIdNumber());
            resident.setLegacyId(therapIdfCSV.getIdNumber());

            final String newLegacyTable = THERAP_LEGACY_TABLE_PREFIX + resolveLegacyTablePostfix(resident.getFacility());
            logger.info("Updating legacy table of resident [{}], [{}] -> [{}]", resident.getId(), resident.getLegacyTable(), newLegacyTable);
            resident.setLegacyTable(newLegacyTable);
        }

        resident.setSocialSecurity(therapIdfCSV.getSsn());
        resident.setBirthDate(therapIdfCSV.getBirthDate().getTime());

        resident.setStatus(therapIdfCSV.getStatus());
        resident.setCitizenship(therapIdfCSV.getCitizenship());

        resident.setBirthPlace(ExchangeStringUtils.joinNotEmpty(", ",
                therapIdfCSV.getBirthPlaceCity(), therapIdfCSV.getBirthPlaceState(), therapIdfCSV.getBirthplaceCountry()));

        //resident.setPrimaryCarePhysician(therapIdfCSV.getPrimaryCarePhysician());
        resident.setMedicaidNumber(therapIdfCSV.getMedicaidNumber());
        resident.setMedicareNumber(therapIdfCSV.getMedicareNumber());

        TimeZone timeZone = StringUtils.isNotEmpty(therapIdfCSV.getTimezone())
                ? TimeZone.getTimeZone(therapIdfCSV.getTimezone()) : null;
        if (timeZone != null) {
            Calendar cal;
            if (therapIdfCSV.getCreated() != null) {
                cal = therapIdfCSV.getCreated();
                cal.setTimeZone(timeZone);
                resident.setDateCreated(cal.getTime());
            }
            if (therapIdfCSV.getUpdated() != null) {
                cal = therapIdfCSV.getUpdated();
                cal.setTimeZone(timeZone);
                resident.setLastUpdated(cal.getTime());
            }
        } else {
            resident.setDateCreated(
                    therapIdfCSV.getCreated() != null ? therapIdfCSV.getCreated().getTime() : new Date());
            resident.setLastUpdated(
                    therapIdfCSV.getUpdated() != null ? therapIdfCSV.getUpdated().getTime() : new Date());
        }

        try {
            CcdCode genderCcdCode = StringUtils.isEmpty(therapIdfCSV.getGender()) ? null
                    : ccdCodeDao.getGenderCcdCode(Gender.getGenderByCode(therapIdfCSV.getGender()));
            resident.setGender(genderCcdCode);
        } catch (NoResultException | IllegalArgumentException e) {
            logger.info("Couldn't find gender [{}]", therapIdfCSV.getGender());
            resident.setGender(null);
        }

        try {
            CcdCode ethnicityCcdCode = StringUtils.isEmpty(therapIdfCSV.getEthnicity()) ? null
                    : ccdCodeDao.getEthnicGroup(therapIdfCSV.getEthnicity());
            resident.setEthnicGroup(ethnicityCcdCode);
        } catch (NoResultException e) {
            logger.info("Couldn't find ethnicity [{}]", therapIdfCSV.getEthnicity());
            resident.setEthnicGroup(null);
        }

        try {
            CcdCode maritalStatusCcdCode = StringUtils.isEmpty(therapIdfCSV.getMaritalStatus()) ? null
                    : ccdCodeDao.getMaritalStatus(
                    "Single".equalsIgnoreCase(therapIdfCSV.getMaritalStatus()) ?
                            "Never Married" : therapIdfCSV.getMaritalStatus()

            );
            resident.setMaritalStatus(maritalStatusCcdCode);
        } catch (NoResultException e) {
            logger.info("Couldn't find marital status [{}]", therapIdfCSV.getMaritalStatus());
            resident.setMaritalStatus(null);
        }

        try {
            CcdCode raceCcdCode = StringUtils.isEmpty(therapIdfCSV.getRace()) ? null
                    : ccdCodeDao.getRaceCcdCode(therapIdfCSV.getRace());
            resident.setRace(raceCcdCode);
        } catch (NoResultException e) {
            logger.info("Couldn't find race [{}]", therapIdfCSV.getRace());
            resident.setRace(null);
        }

        try {
            CcdCode religionCcdCode = StringUtils.isEmpty(therapIdfCSV.getReligion()) ? null
                    : ccdCodeDao.getReligionCcdCode(therapIdfCSV.getReligion());
            resident.setReligion(religionCcdCode);
        } catch (NoResultException e) {
            logger.info("Couldn't find religion [{}]", therapIdfCSV.getReligion());
            resident.setReligion(null);
        }

        InNetworkInsurance inNetworkInsurance = null;
        InsurancePlan insurancePlan = null;
        if (StringUtils.isEmpty(therapIdfCSV.getMedPlanIssuer())) {
            resident.setInNetworkInsurance(null);
            resident.setInsurancePlan(null);
        } else {
            inNetworkInsurance = inNetworkInsuranceDao.getByDisplayName(therapIdfCSV.getMedPlanIssuer());
            resident.setInNetworkInsurance(inNetworkInsurance);

            if (inNetworkInsurance != null && StringUtils.isNotEmpty(therapIdfCSV.getMedPlanName())) {
                resident.setInsurancePlanName(therapIdfCSV.getMedPlanName());
            } else {
                resident.setInsurancePlan(null);
            }
        }
        if (StringUtils.isNotEmpty(therapIdfCSV.getMedPlanIssuer())
                && inNetworkInsurance == null)  {
            saveTherapUnknownNetwork(therapIdfCSV.getMedPlanId(), therapIdfCSV.getMedPlanIssuer(),
                    therapIdfCSV.getMedPlanName());
        }
    }

    private Person createOrUpdatePerson(TherapIdfCSV therapIdfCSV, Resident resident) {
        Person person = resident.getPerson();
        if (person == null) {
            person = new Person();
            person.setDatabaseId(resident.getDatabase().getId());
            person.setDatabase(resident.getDatabase());
            person.setLegacyTable(CareCoordinationConstants.RBA_NAME_LEGACY_TABLE);
            CareCoordinationConstants.setLegacyId(person);
        }
        createOrUpdateName(person, therapIdfCSV);
        createOrUpdatePersonAddress(person, therapIdfCSV);

        return person;
    }

    private void createOrUpdateName(Person person, TherapIdfCSV therapIdfCSV) {
        Name name = findNameWithLUseCode(person);
        if (name == null) {
            name = new Name();
            name.setNameUse(NAME_USE_CODE);
            name.setDatabase(person.getDatabase());
            name.setDatabaseId(person.getDatabaseId());
            name.setLegacyTable(CareCoordinationConstants.RBA_NAME_LEGACY_TABLE);

            if (person.getNames() == null) {
                person.setNames(new ArrayList<Name>());
            }
            person.getNames().add(name);
            name.setPerson(person);
        }
        CareCoordinationConstants.setLegacyIdFromParent(name, person);
        fillName(name, therapIdfCSV);
    }

    private Name findNameWithLUseCode(Person person) {
        if (person.getNames() == null) {
            return null;
        }
        for (Name name : person.getNames()) {
            if (NAME_USE_CODE.equals(name.getNameUse())) {
                return name;
            }
        }
        return null;
    }

    private void fillName(Name name, TherapIdfCSV therapIdfCSV) {
        name.setGiven(therapIdfCSV.getFirstName());
        name.setFamily(therapIdfCSV.getLastName());
        name.setMiddle(therapIdfCSV.getMiddleName());
        name.setSuffix(therapIdfCSV.getSuffix());
        name.setGivenNormalized(CareCoordinationUtils.normalizeName(therapIdfCSV.getFirstName()));
        name.setFamilyNormalized(CareCoordinationUtils.normalizeName(therapIdfCSV.getLastName()));
    }

    private void createOrUpdatePersonAddress(Person person, TherapIdfCSV therapIdfCSV) {
        if (person.getAddresses() == null) {
            person.setAddresses(new ArrayList<PersonAddress>());
        }

        PersonAddress personAddress;
        if (person.getAddresses().isEmpty()) {
            personAddress = new PersonAddress();

            personAddress.setDatabase(person.getDatabase());
            personAddress.setDatabaseId(person.getDatabaseId());

            CareCoordinationConstants.setLegacyIdFromParent(personAddress, person);
            personAddress.setLegacyTable(CareCoordinationConstants.RBA_NAME_LEGACY_TABLE);

            personAddress.setPerson(person);
            person.getAddresses().add(personAddress);
        } else {
            personAddress = person.getAddresses().get(0);
        }

        fillPersonAddress(personAddress, therapIdfCSV);
    }

    private PersonAddress fillPersonAddress(PersonAddress personAddress, TherapIdfCSV therapIdfCSV) {
        personAddress.setStreetAddress(ExchangeStringUtils.joinNotEmpty(" ", therapIdfCSV.getStreet1(), therapIdfCSV.getStreet2()));
        personAddress.setCity(therapIdfCSV.getCity());
        personAddress.setState(therapIdfCSV.getState());
        personAddress.setPostalCode(therapIdfCSV.getZip());
        personAddress.setCountry(therapIdfCSV.getCountry());

        return personAddress;
    }

    private void saveTherapUnknownNetwork(String medPlanId, String medPlanIssuer, String medPlanName) {
        try {
            TherapUnknownNetworkPlan therapUnknownNetworkPlan = therapUnknownNetworkPlanDao
                    .findByMedPlanIdAndMedPlanNameAndMedPlanIssuer(medPlanId, medPlanName, medPlanIssuer);
            if (therapUnknownNetworkPlan == null) {
                therapUnknownNetworkPlan = new TherapUnknownNetworkPlan();
                therapUnknownNetworkPlan.setMedPlanId(medPlanId);
                therapUnknownNetworkPlan.setMedPlanIssuer(medPlanIssuer);
                therapUnknownNetworkPlan.setMedPlanName(medPlanName);
                therapUnknownNetworkPlanDao.save(therapUnknownNetworkPlan);
            }
        } catch (Exception e) {
            logger.error("Exception occurred while saving TherapUnknownNetworkPlan, due to - {} ",
                    ExceptionUtils.getStackTrace(e));
        }
    }

    private Resident updateAdmittanceHistory(Resident resident, Calendar admissionCal) {
        //date didn't come in idf
        if (admissionCal == null) {
            return resident;
        }
        final Date admissionDate = admissionCal.getTime();

        //save old date to history if necessary
        saveAdmitIfNotPresent(resident, resident.getAdmitDate());

        //if old date equals to new date - no more actions needed
        if (resident.getAdmitDate() != null && resident.getAdmitDate().equals(admissionDate)) {
            return resident;
        }

        //save new date to history if necessary
        saveAdmitIfNotPresent(resident, admissionDate);

        //update admitDate column
        if (resident.getAdmitDate() == null || resident.getAdmitDate().before(admissionDate)) {
            resident.setAdmitDate(admissionDate);
            return residentService.updateResident(resident);
        }

        return resident;
    }

    private void saveAdmitIfNotPresent(Resident resident, Date date) {
        if (date != null && jpaAdmittanceHistoryDao.getByResidentIdAndAdmitDate(resident.getId(), date) == null) {
            final AdmittanceHistory admittanceHistory = new AdmittanceHistory();
            admittanceHistory.setAdmitDate(date);
            admittanceHistory.setResident(resident);
            admittanceHistory.setOrganizationId(resident.getFacility().getId());
            admittanceHistory.setDatabase(resident.getDatabase());
            admittanceHistory.setDatabaseId(resident.getDatabase().getId());
            jpaAdmittanceHistoryDao.save(admittanceHistory);
        }
    }
}
