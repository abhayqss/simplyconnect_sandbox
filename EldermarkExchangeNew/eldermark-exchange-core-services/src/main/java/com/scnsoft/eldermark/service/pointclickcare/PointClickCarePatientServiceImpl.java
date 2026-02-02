package com.scnsoft.eldermark.service.pointclickcare;

import com.scnsoft.eldermark.dao.*;
import com.scnsoft.eldermark.dto.pointclickcare.PointClickCareUnknownLocationException;
import com.scnsoft.eldermark.dto.pointclickcare.model.PCCSwitcherooCodeableConcept;
import com.scnsoft.eldermark.dto.pointclickcare.model.patient.PCCPatientDetails;
import com.scnsoft.eldermark.dto.pointclickcare.model.patient.PccPatientLegalMailingAddress;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.document.ccd.ConcreteCcdCode;
import com.scnsoft.eldermark.entity.document.facesheet.Language;
import com.scnsoft.eldermark.service.ClientHieConsentDefaultPolicyService;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.ClientValidationViolation;
import com.scnsoft.eldermark.service.basic.CareCoordinationConstants;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;
import java.util.*;
import java.util.stream.Stream;

@Service
@Transactional
@ConditionalOnProperty(value = "pcc.integration.enabled", havingValue = "true")
public class PointClickCarePatientServiceImpl implements PointClickCarePatientService {
    static final String PCC_INTEGRATION_LEGACY_TABLE = "PCC_INTEGRATION";
    private static final Map<PCCPatientDetails.Gender, String> GENDER_CODE_MAPPING = Map.of(
            PCCPatientDetails.Gender.MALE, "M",
            PCCPatientDetails.Gender.FEMALE, "F",
            PCCPatientDetails.Gender.UNKNOWN, "UN"
    );
    private final static Set<String> ACTIVE_STATUSES = Set.of(
            PCCPatientDetails.PATIENT_STATUS_NEW,
            PCCPatientDetails.PATIENT_STATUS_CURRENT
    );
    private final Logger logger = LoggerFactory.getLogger(PointClickCarePatientServiceImpl.class);
    private final PointClickCareApiGateway pointClickCareApiGateway;
    private final ClientDao clientDao;
    private final OrganizationDao organizationDao;
    private final CommunityDao communityDao;
    private final CcdCodeDao ccdCodeDao;
    private final ConcreteCcdCodeDao concreteCcdCodeDao;
    private final ClientService clientService;
    private final PointClickCareSpecifications pccSpecifications;

    private final ClientHieConsentDefaultPolicyService clientHieConsentDefaultPolicyService;

    public PointClickCarePatientServiceImpl(PointClickCareApiGateway pointClickCareApiGateway,
                                            ClientDao clientDao,
                                            OrganizationDao organizationDao,
                                            CommunityDao communityDao,
                                            CcdCodeDao ccdCodeDao,
                                            ConcreteCcdCodeDao concreteCcdCodeDao,
                                            ClientService clientService,
                                            PointClickCareSpecifications pccSpecifications,
                                            ClientHieConsentDefaultPolicyService clientHieConsentDefaultPolicyService) {
        this.pointClickCareApiGateway = pointClickCareApiGateway;
        this.clientDao = clientDao;
        this.organizationDao = organizationDao;
        this.communityDao = communityDao;
        this.ccdCodeDao = ccdCodeDao;
        this.concreteCcdCodeDao = concreteCcdCodeDao;
        this.clientService = clientService;
        this.pccSpecifications = pccSpecifications;
        this.clientHieConsentDefaultPolicyService = clientHieConsentDefaultPolicyService;
    }

    @Override
    public Client createOrUpdateClient(String pccOrgUuid, Long pccPatientId) {
        var pccPatient = pointClickCareApiGateway.patientById(pccOrgUuid, pccPatientId);
        var client = findClientByPCCOrgUuidAndPatientId(pccOrgUuid, pccPatientId)
                .orElseGet(() -> prepareNewClient(pccOrgUuid, pccPatientId, pccPatient.getFacId()));

        client = updateClient(client, pccPatient);
        logger.info("PointClickCare Patient: Updated client with id = {} for pccPatientId = {}, orgUuid = {}",
                client.getId(), pccPatientId, pccOrgUuid);

        return client;
    }

    private Optional<Client> findClientByPCCOrgUuidAndPatientId(String pccOrgUuid, Long pccPatientId) {
        var client = clientDao.findFirst(
                pccSpecifications.clientByPccOrgUuidAndPccPatientId(pccOrgUuid, pccPatientId),
                Client.class
        );
        client.ifPresent(c -> logger.info("PointClickCare Patient: Found client with id = {} for pccPatientId = {}, orgUuid = {}",
                c.getId(), pccPatientId, pccOrgUuid));
        return client;
    }

    private Client prepareNewClient(String pccOrgUuid, Long pccPatientId, Long pccFacId) {
        logger.info("PointClickCare Patient: Creating new patient");
        var client = new Client();
        CareCoordinationConstants.setLegacyId(client);
        client.setLegacyTable(PCC_INTEGRATION_LEGACY_TABLE);

        var organization = getOrganization(pccOrgUuid).
                orElseThrow(() -> new PointClickCareUnknownLocationException("Unknown organization with pcc uuid " + pccOrgUuid));
        client.setOrganization(organization);
        client.setOrganizationId(organization.getId());

        var community = findCommunity(organization.getId(), pccFacId)
                .orElseThrow(() -> new PointClickCareUnknownLocationException("Unknown Community with pcc facility id " + pccFacId + ", organizationId " + organization.getId() + ", pcc uuid " + pccOrgUuid));
        client.setCommunity(community);
        client.setCommunityId(community.getId());

        var person = CareCoordinationUtils.createNewPerson(organization, PCC_INTEGRATION_LEGACY_TABLE);
        client.setPerson(person);

        person.setAddresses(new ArrayList<>());
        person.setNames(new ArrayList<>());
        person.setTelecoms(new ArrayList<>());

        clientHieConsentDefaultPolicyService.fillDefaultPolicy(client);

        client.setPccPatientId(pccPatientId);

        return client;
    }

    private Optional<Organization> getOrganization(String pccOrgUuid) {
        return organizationDao.findFirst(
                pccSpecifications.orgByPccOrgUuid(pccOrgUuid),
                Organization.class
        );
    }

    private Optional<Community> findCommunity(Long organizationId, Long pccFacilityId) {
        return communityDao.findFirst(
                pccSpecifications.comunityByOrgIdAndPccFacilityId(organizationId, pccFacilityId),
                Community.class
        );
    }

    private Client updateClient(Client client, PCCPatientDetails pccPatientDetails) {
        if (pccPatientDetails.getFacId() == null) {
            throw new PointClickCareUnknownLocationException("Patient " + pccPatientDetails.getPatientId() + " facId is null");
        }
        if (!pccPatientDetails.getFacId().equals(client.getCommunity().getPccFacilityId())) {
            throw new PointClickCareUnknownLocationException("Patient " + pccPatientDetails.getPatientId() + " facId " + pccPatientDetails.getFacId() + " doesn't match with current " + client.getCommunity().getPccFacilityId());
        }

        var facilityTimeZone = Optional.ofNullable(client.getCommunity().getPccFacilityTimezone())
                .map(zone -> ZoneId.of(zone, ZoneId.SHORT_IDS))
                .orElseGet(ZoneId::systemDefault);

        client.setAdmitDate(
                Optional.ofNullable(pccPatientDetails.getAdmissionDateTime())
                        .or(() -> Optional.ofNullable(pccPatientDetails.getAdmissionDate())
                                .map(LocalDate::atStartOfDay)
                                .map(ldt -> ldt.atZone(facilityTimeZone))
                                .map(ChronoZonedDateTime::toInstant))
                        .orElse(null)
        );
        client.setBirthDate(pccPatientDetails.getBirthDate());
        client.setCitizenship(pccPatientDetails.getCitizenship());
        client.setDeathDate(pccPatientDetails.getDeathDateTime());
        client.setDeathIndicator(pccPatientDetails.getDeceased());
        client.setDischargeDate(
                Optional.ofNullable(pccPatientDetails.getDischargeDate())
                        .map(ldt -> ldt.atStartOfDay(facilityTimeZone)).map(ChronoZonedDateTime::toInstant)
                        .orElse(null)
        );

        client.setFirstName(pccPatientDetails.getFirstName());
        client.setGender(mapGender(pccPatientDetails.getGender()));
        client.setLastName(pccPatientDetails.getLastName());
        client.setMedicaidNumber(pccPatientDetails.getMedicaidNumber());
        client.setMedicalRecordNumber(pccPatientDetails.getMedicalRecordNumber());
        client.setMedicareNumber(Optional
                .ofNullable(pccPatientDetails.getMedicareBeneficiaryIdentifier())
                .orElse(pccPatientDetails.getMedicareNumber())
        );
        client.setRace(mapRace(pccPatientDetails.getRaceCode()));
        client.setEthnicGroup(mapEthnicity(pccPatientDetails.getEthnicityCode()));
        client.setMaritalStatus(findMaritalStatus(pccPatientDetails.getMaritalStatus()));
        client.setMiddleName(pccPatientDetails.getMiddleName());

        client.setActive(mapStatus(pccPatientDetails.getPatientStatus()));
        client.setPreferredName(pccPatientDetails.getPreferredName());

        client.setOutpatient(pccPatientDetails.isOutpatient());
        client.setMaidenName(pccPatientDetails.getMaidenName());

        if ("USA".equals(client.getCommunity().getPccFacilityCountry()) && StringUtils.isNotBlank(pccPatientDetails.getSocialBeneficiaryIdentifier())) {
            var ssn = CareCoordinationUtils.normalizePhone(pccPatientDetails.getSocialBeneficiaryIdentifier());
            client.setSocialSecurity(ssn);
        }

        updateName(client.getPerson(), pccPatientDetails);
        updateTelecoms(client.getPerson(), pccPatientDetails);
        createAddress(client.getPerson(), pccPatientDetails.getLegalMailingAddress());
        updateLanguage(client, pccPatientDetails);

        cleanupViolatingFields(client);

        return clientService.save(client);
    }

    private void cleanupViolatingFields(Client client) {
        var violations = clientService.runValidation(client, false);

        if (violations.isEmpty()) {
            return;
        }

        if (violations.contains(ClientValidationViolation.EMAIL)) {
            PersonTelecomUtils.find(client.getPerson(), PersonTelecomCode.EMAIL).ifPresent(
                    emailTelecom -> {
                        emailTelecom.setPerson(null);
                        client.getPerson().getTelecoms().remove(emailTelecom);
                    }
            );
            logger.info("PointClickCare Patient: Violation detected - {}",
                    ClientValidationViolation.EMAIL.getErrorMessage());
        }

        if (violations.contains(ClientValidationViolation.MEDICARE_NUMBER)) {
            client.setMedicareNumber(null);
            logger.info("PointClickCare Patient: Violation detected - {}",
                    ClientValidationViolation.MEDICARE_NUMBER.getErrorMessage());
        }

        if (violations.contains(ClientValidationViolation.MEDICAID_NUMBER)) {
            client.setMedicaidNumber(null);
            logger.info("PointClickCare Patient: Violation detected - {}",
                    ClientValidationViolation.MEDICAID_NUMBER.getErrorMessage());
        }

        if (violations.contains(ClientValidationViolation.SSN)) {
            client.setSocialSecurity(null);
            logger.info("PointClickCare Patient: Violation detected - {}",
                    ClientValidationViolation.SSN.getErrorMessage());
        }

        if (violations.contains(ClientValidationViolation.BIRTH_DATE)) {
            client.setBirthDate(null);
            logger.info("PointClickCare Patient: Violation detected - {}",
                    ClientValidationViolation.BIRTH_DATE.getErrorMessage());
        }
    }

    private Name createName(Person person) {
        var name = new Name();
        name.setPerson(person);

        if (person.getNames() == null) {
            person.setNames(new ArrayList<>());
        }

        person.getNames().add(name);

        name.setOrganization(person.getOrganization());
        CareCoordinationConstants.setLegacyId(name);
        name.setLegacyTable(PCC_INTEGRATION_LEGACY_TABLE);

        return name;
    }


    private CcdCode findMaritalStatus(String maritalStatus) {
        if (StringUtils.isEmpty(maritalStatus)) {
            return null;
        }
        return ccdCodeDao.findFirstByDisplayNameAndCodeSystem(maritalStatus, CodeSystem.MARITAL_STATUS.getOid());
    }

    private void createAddress(Person person, PccPatientLegalMailingAddress legalMailingAddress) {
        if (CollectionUtils.isNotEmpty(person.getAddresses())) {
            return;
        }

        if (legalMailingAddress == null || StringUtils.isAllEmpty(
                legalMailingAddress.getAddressLine1(),
                legalMailingAddress.getAddressLine2(),
                legalMailingAddress.getCity(),
                legalMailingAddress.getCountry(),
                legalMailingAddress.getPostalCode(),
                legalMailingAddress.getState()
        )) {
            return;
        }

        if (person.getAddresses() == null) {
            person.setAddresses(new ArrayList<>());
        }

        var personAddress = new PersonAddress();
        CareCoordinationConstants.setLegacyId(personAddress);
        personAddress.setPerson(person);
        personAddress.setOrganization(person.getOrganization());
        personAddress.setLegacyTable(PCC_INTEGRATION_LEGACY_TABLE);
        personAddress.setPostalAddressUse("HP");


        personAddress.setStreetAddress(CareCoordinationUtils.concat(" ",
                Stream.of(legalMailingAddress.getAddressLine1(), legalMailingAddress.getAddressLine2())));
        personAddress.setCity(legalMailingAddress.getCity());
        personAddress.setCountry(legalMailingAddress.getCountry());
        personAddress.setPostalCode(legalMailingAddress.getPostalCode());
        personAddress.setState(legalMailingAddress.getState());

        person.getAddresses().add(personAddress);
    }

    private void updateLanguage(Client client, PCCPatientDetails pccPatientDetails) {
        var languages = client.getLanguages();
        if (StringUtils.isNotEmpty(pccPatientDetails.getLanguageCode()) && StringUtils.isNotEmpty(pccPatientDetails.getLanguageDesc())) {
            if (languages == null) {
                languages = new ArrayList<>();
                client.setLanguages(languages);
            }

            if (languages.stream()
                    .map(Language::getCode)
                    .filter(Objects::nonNull)
                    .filter(langCcdCode -> CodeSystem.LANGUAGE.getOid().equals(langCcdCode.getCodeSystem()))
                    .noneMatch(langCcdCode -> pccPatientDetails.getLanguageCode().equals(langCcdCode.getCode()))) {
                var language = new Language();
                language.setClient(client);
                language.setLegacyId(0);
                language.setPreferenceInd(true);
                language.setOrganization(client.getOrganization());
                language.setCode(findOrCreateLanguageCode(pccPatientDetails.getLanguageCode(), pccPatientDetails.getLanguageDesc()));

                languages.add(language);
            }
        }
    }

    private CcdCode mapGender(PCCPatientDetails.Gender gender) {
        if (gender == null) {
            return null;
        }

        var code = GENDER_CODE_MAPPING.getOrDefault(gender, null);
        if (code == null) {
            return null;
        }

        return ccdCodeDao.findFirstByCodeAndCodeSystem(code, CodeSystem.ADMINISTRATIVE_GENDER.getOid());
    }

    private CcdCode findOrCreateLanguageCode(String languageCode, String languageDesc) {
        return Optional.ofNullable(ccdCodeDao.findFirstByCodeAndCodeSystem(languageCode, CodeSystem.LANGUAGE.getOid()))
                .orElseGet(() -> {
                    var concreteCcdCode = new ConcreteCcdCode();
                    concreteCcdCode.setCode(languageCode);
                    concreteCcdCode.setDisplayName(languageDesc);
                    concreteCcdCode.setCodeSystem(CodeSystem.LANGUAGE.getOid());
                    concreteCcdCode.setCodeSystemName(CodeSystem.LANGUAGE.getDisplayName());
                    concreteCcdCode.setValueSet("2.16.840.1.113883.1.11.11526");
                    concreteCcdCode.setValueSetName("Human Language");
                    concreteCcdCode = concreteCcdCodeDao.save(concreteCcdCode);
                    return ccdCodeDao.getOne(concreteCcdCode.getId());
                });
    }

    private CcdCode mapRace(PCCSwitcherooCodeableConcept raceCode) {
        return mapRaceEthnicity(raceCode, "http://phinvads.cdc.gov");
    }

    private CcdCode mapEthnicity(PCCSwitcherooCodeableConcept raceCode) {
        return mapRaceEthnicity(raceCode, "http://snomed.info/sct");
    }

    private CcdCode mapRaceEthnicity(PCCSwitcherooCodeableConcept raceCode, String codeSystemToUse) {
        if (raceCode == null || CollectionUtils.isEmpty(raceCode.getCodings())) {
            return null;
        }
        return raceCode.getCodings().stream()
                .filter(pccRace -> codeSystemToUse.equals(pccRace.getSystem()) && StringUtils.isNotEmpty(pccRace.getCode()))
                .map(pccRace -> ccdCodeDao.findFirstByCodeAndCodeSystem(pccRace.getCode(), CodeSystem.RACE_AND_ETHNICITY_CDC.getOid()))
                .findFirst()
                .orElse(null);
    }

    private Boolean mapStatus(String patientStatus) {
        return StringUtils.isNotEmpty(patientStatus) && ACTIVE_STATUSES.contains(patientStatus);
    }

    private void updateName(Person person, PCCPatientDetails pccPatientDetails) {
        if (!StringUtils.isAllEmpty(pccPatientDetails.getFirstName(), pccPatientDetails.getLastName())) {
            var name = person.getNames().stream()
                    .filter(n -> "L".equals(n.getNameUse()))
                    .findFirst()
                    .orElseGet(() -> CareCoordinationUtils.createAndAddName(person, pccPatientDetails.getFirstName(), pccPatientDetails.getLastName(), PCC_INTEGRATION_LEGACY_TABLE));
            name.setMiddle(pccPatientDetails.getMiddleName());
            name.setPreferredName(pccPatientDetails.getPreferredName());
            name.setPrefix(pccPatientDetails.getPrefix());
            name.setSuffix(pccPatientDetails.getSuffix());
        }
    }

    private void updateTelecoms(Person person, PCCPatientDetails pccPatientDetails) {
        updateEmail(person, pccPatientDetails);
        updateHomePhone(person, pccPatientDetails);

    }

    private void updateEmail(Person person, PCCPatientDetails pccPatientDetails) {
        if (StringUtils.isNotEmpty(pccPatientDetails.getEmail())) {
            var telecom = findOrCreateTelecom(person, PersonTelecomCode.EMAIL);
            telecom.setValue(pccPatientDetails.getEmail());
        }
    }

    private void updateHomePhone(Person person, PCCPatientDetails pccPatientDetails) {
        if ("home".equals(pccPatientDetails.getItuPhone()) && StringUtils.isNotEmpty(pccPatientDetails.getItuPhone())) {
            var telecom = findOrCreateTelecom(person, PersonTelecomCode.HP);
            telecom.setValue("1" + CareCoordinationUtils.normalizePhone(pccPatientDetails.getItuPhone()));
        }
    }

    private PersonTelecom findOrCreateTelecom(Person person, PersonTelecomCode code) {
        return PersonTelecomUtils.find(person, code)
                .orElseGet(() -> {
                    var homePhone = new PersonTelecom();
                    homePhone.setSyncQualifier(code.getCode());
                    homePhone.setUseCode(code.name());
                    homePhone.setOrganization(person.getOrganization());
                    homePhone.setOrganizationId(person.getOrganizationId());
                    homePhone.setLegacyTable(PCC_INTEGRATION_LEGACY_TABLE);
                    com.scnsoft.eldermark.service.CareCoordinationConstants.setLegacyId(homePhone);

                    homePhone.setPerson(person);
                    person.getTelecoms().add(homePhone);

                    return homePhone;
                });
    }
}

