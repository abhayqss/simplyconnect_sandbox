package com.scnsoft.eldermark.consana.sync.server.services.converter;

import com.scnsoft.eldermark.consana.sync.common.entity.HieConsentPolicyType;
import com.scnsoft.eldermark.consana.sync.server.dao.*;
import com.scnsoft.eldermark.consana.sync.server.model.entity.BasicEntity;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Database;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;
import com.scnsoft.eldermark.consana.sync.server.utils.FhirConversionUtils;
import org.hl7.fhir.instance.model.BooleanType;
import org.hl7.fhir.instance.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

import static com.scnsoft.eldermark.consana.sync.server.constants.ConsanaSyncApiReceivePatientConstants.*;
import static com.scnsoft.eldermark.consana.sync.server.constants.FhirConstants.CONSANA_LEGACY_TABLE;
import static com.scnsoft.eldermark.consana.sync.server.constants.FhirConstants.DATE_TIME_TYPE;
import static com.scnsoft.eldermark.consana.sync.server.model.enums.IdentifierCode.*;
import static com.scnsoft.eldermark.consana.sync.server.utils.FhirConversionUtils.*;
import static java.time.Instant.now;
import static java.util.Optional.ofNullable;

@Component
@Transactional(propagation = Propagation.SUPPORTS, noRollbackFor = Exception.class)
public class PatientToResidentConverter {
    String OBTAINED_FROM_STATE_POLICY_VALUE = "State Policy";

    @Autowired
    private Clock clock;

    @Autowired
    private DatabaseDao databaseDao;

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private FhirConversionUtils fhirConversionUtils;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private OrganizationHieConsentPolicyDao communityHieConsentPolicyDao;

    public Resident convert(Patient patient, String patientId, String consanaOrgId, String consanaCommunityId) {
        var resident = new Resident();
        var community = organizationDao.getFirstByConsanaOrgIdAndDatabaseConsanaXOwningId(consanaCommunityId, consanaOrgId);
        var database = community.getDatabase();

        resident.setDatabase(database);
        updateResidentNames(resident, patient);
        resident.setFacility(community);
        resident.setBirthDate(patient.getBirthDate());
        resident.setGender(fhirConversionUtils.convertGender(patient.getGender()));
        resident.setMaritalStatus(fhirConversionUtils.convertMaritalStatus(patient.getMaritalStatus()));
        resident.setSocialSecurity(fetchIdentifier(patient, SSN));
        resident.setRace(fhirConversionUtils.convertRace(patient));
        resident.setEthnicGroup(fhirConversionUtils.convertEthnicGroup(patient));
        resident.setReligion(fhirConversionUtils.convertReligion(patient));
        resident.setMedicaidNumber(fetchIdentifier(patient, MEDICAID));
        resident.setMedicalRecordNumber(fetchIdentifier(patient, MRN));
        resident.setMedicareNumber(fetchIdentifier(patient, MEDICARE));
        resident.setMemberNumber(fetchIdentifier(patient, MEMBER_NUMBER));
        resident.setCreatedById(getCreatedById());
        resident.setIsOptOut(false);
        resident.setConsanaXrefId(patientId);
        resident.setLegacyId(LEGACY_ID_PREFIX);
        resident.setLegacyTable(CONSANA_LEGACY_TABLE);
        resident.setDateCreated(now(clock));
        resident.setLastUpdated(now(clock));
        resident.setAdmitDate(getAdmitDate(patient));
        resident.setActive(true);
        resident.setHieConsentPolicyObtainedFrom(OBTAINED_FROM_STATE_POLICY_VALUE);
        resident.setHieConsentPolicyUpdateDateTime(Instant.now());
        resident.setHieConsentPolicyType(resolveDefaultHieConsentPolicyType(community.getId()));

        Boolean isDateTimeType = ofNullable(patient.getDeceased())
                .map(type -> type.toString().contains(DATE_TIME_TYPE))
                .orElse(null);
        ofNullable(isDateTimeType).ifPresent(isDateTime -> {
            if (isDateTimeType){
                resident.setDeathDate(getDeathDate(patient));
                resident.setDeathIndicator(ofNullable(getDeathDate(patient)).isPresent());
            } else {
                BooleanType booleanType = patient.getDeceased().castToBoolean(patient.getDeceased());
                resident.setDeathIndicator(ofNullable(booleanType).map(b -> b.booleanValue()).orElse(null));
            }
        });
        resident.setPerson(createOrUpdatePerson(resident, patient));
        resident.setCitizenship(getCitizenship(patient));
        //resident.setLanguages();
        return resident;
    }

    private HieConsentPolicyType resolveDefaultHieConsentPolicyType(Long communityId) {
        return communityHieConsentPolicyDao.findByCommunityIdAndArchived(
                        communityId, false, CommunityHieConsentPolicyTypeAware.class)
                .map(CommunityHieConsentPolicyTypeAware::getType)
                .orElse(HieConsentPolicyType.OPT_OUT);
    }

    private Long getCreatedById(){
        return ofNullable(databaseDao.getFirstByAlternativeId(ORGANIZATION_ALTERNATIVE_ID))
                .map(Database::getId)
                .map(dbId -> employeeDao.getFirstByLoginAndDatabaseId(EMPLOYEE_LOGIN, dbId))
                .map(BasicEntity::getId)
                .orElse(null);
    }
}
