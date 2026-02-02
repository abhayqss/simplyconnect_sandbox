package com.scnsoft.eldermark.service.document.templates.cda.factory.header;

import com.scnsoft.eldermark.beans.ValueSetEnum;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.document.ccd.Guardian;
import com.scnsoft.eldermark.entity.document.facesheet.Language;
import com.scnsoft.eldermark.service.ClientHieConsentDefaultPolicyService;
import com.scnsoft.eldermark.service.document.cda.CcdCodeFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.entries.CdaSectionEntryFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.RequiredTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.PersonFactory;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The recordTarget records the patient whose health information is described by the clinical document.
 *
 * @see Client
 * @see Guardian
 * @see Person
 * @see PersonAddress
 * @see PersonTelecom
 * @see Language
 * @see Name
 */
@Component
public class RecordTargetFactoryImpl extends RequiredTemplateFactory implements RecordTargetFactory {

    private static CdaSectionEntryFactory cdaSectionEntryFactory = CdaSectionEntryFactory.INSTANCE;

    @Autowired
    private CcdCodeFactory ccdCodeFactory;

    @Autowired
    private ClientHieConsentDefaultPolicyService clientHieConsentDefaultPolicyService;

    private static final Logger logger = LoggerFactory.getLogger(RecordTargetFactoryImpl.class);
    private static final String LEGACY_TABLE = "Resident_NWHIN";

    @Override
    public Client parseSection(Community community, PatientRole ccdPatientRole) {
        if (!CcdParseUtils.hasContent(ccdPatientRole)) {
            return null;
        }
        checkNotNull(community);

        final Client result = new Client();
        result.setCommunity(community);
        result.setLegacyTable(LEGACY_TABLE);
        result.setOrganization(community.getOrganization());
        result.setOrganizationId(community.getOrganizationId());

        final Patient patient = ccdPatientRole.getPatient();

        // Note: Real resident legacy ID (from ResidentDTO) will overwrite this value later in CcdMediator service
        result.setLegacyId(CcdParseUtils.getFirstIdExtensionStr(ccdPatientRole.getIds()));

        // TODO used default organization
        final Community providerOrganization = CcdTransform.toCommunity(ccdPatientRole.getProviderOrganization(),
                community.getOrganization(), LEGACY_TABLE);
        result.setProviderOrganization(community);
        if (patient != null) {
            result.setBirthDate(CcdParseUtils.convertTsToLocalDate(patient.getBirthTime()));
            result.setBirthplaceAddress(convert(patient.getBirthplace(), result));
            // TODO result.setBirthPlace();

            result.setGender(ccdCodeFactory.convert(patient.getAdministrativeGenderCode()));
            result.setMaritalStatus(ccdCodeFactory.convert(patient.getMaritalStatusCode()));
            result.setReligion(ccdCodeFactory.convert(patient.getReligiousAffiliationCode()));
            result.setRace(ccdCodeFactory.convert(patient.getRaceCode()));
            result.setEthnicGroup(ccdCodeFactory.convert(patient.getEthnicGroupCode()));

            if (!CollectionUtils.isEmpty(patient.getGuardians())) {
                List<Guardian> guardians = new ArrayList<>();
                for (org.eclipse.mdht.uml.cda.Guardian ccdGuardian : patient.getGuardians()) {
                    final Person guardianPerson = CcdParseUtils.createPerson(ccdGuardian, community.getOrganization(),
                            LEGACY_TABLE);
                    if (guardianPerson != null) {
                        Guardian guardian = new Guardian();
                        guardian.setOrganization(result.getOrganization());
                        guardian.setOrganizationId(result.getOrganizationId());
                        guardian.setClient(result);
                        guardian.setPerson(guardianPerson);
                        guardian.setRelationship(ccdCodeFactory.convert(ccdGuardian.getCode()));
                        guardians.add(guardian);
                    }
                }
                result.setGuardians(guardians);
            }

            if (!CollectionUtils.isEmpty(patient.getLanguageCommunications())) {
                final List<Language> languages = new ArrayList<>();
                for (LanguageCommunication languageCommunication : patient.getLanguageCommunications()) {
                    Language language = new Language();
                    language.setClient(result);
                    language.setOrganization(result.getOrganization());
                    language.setOrganizationId(result.getOrganizationId());
                    II ii = languageCommunication.getTypeId();
                    if (ii != null && ii.getExtension() != null) {
                        language.setLegacyId(Long.parseLong(ii.getExtension()));
                    }

                    language.setCode(ccdCodeFactory.convert(languageCommunication.getLanguageCode()));
                    language.setAbilityMode(ccdCodeFactory.convert(languageCommunication.getModeCode()));
                    language.setAbilityProficiency(ccdCodeFactory.convert(languageCommunication.getProficiencyLevelCode()));
                    BL preferenceInd = languageCommunication.getPreferenceInd();
                    if (CcdParseUtils.hasContent(preferenceInd)) {
                        language.setPreferenceInd(preferenceInd.getValue());
                    }

                    languages.add(language);
                }
                result.setLanguages(languages);
            }
        }

        final Person person = PersonFactory.parse(ccdPatientRole, community.getOrganization(), LEGACY_TABLE);
        result.setPerson(person);

        clientHieConsentDefaultPolicyService.fillDefaultPolicy(result);

        return result;
    }

    private static BirthplaceAddress convert(Birthplace birthplace, Client client) {
        if (!CcdParseUtils.hasContent(birthplace) || !CcdParseUtils.hasContent(birthplace.getPlace())) {
            return null;
        }
        return CcdParseUtils.createAddress(birthplace.getPlace().getAddr(), client);
    }

    @Override
    public PatientRole buildTemplateInstance(Client client) {
        checkNotNull(client);

        final PatientRole patientRole = CDAFactory.eINSTANCE.createPatientRole();

        // create ccd template
        patientRole.getIds().add(CcdUtils.getId(client.getId()));

        final Patient patient = CDAFactory.eINSTANCE.createPatient();
        final Person ccdPerson = client.getPerson();
        if (ccdPerson != null) {
            if (CollectionUtils.isNotEmpty(ccdPerson.getNames())) {
                for (Name name : client.getPerson().getNames()) {
                    if ("L".equals(name.getNameUse())) {
                        CcdUtils.addConvertedName(patient.getNames(), name);
                    }
                }
            } else {
                patient.getNames().add(CcdUtils.getNullName());
            }

            CcdUtils.addConvertedAddresses(ccdPerson.getAddresses(), patientRole.getAddrs(), true);
            CcdUtils.addConvertedTelecoms(ccdPerson.getTelecoms(), patientRole.getTelecoms(), true);
        } else {
            patient.getNames().add(CcdUtils.getNullName());
            patientRole.getAddrs().add(CcdUtils.getNullAddress());
            patientRole.getTelecoms().add(CcdUtils.getNullTelecom());
        }

        patient.setAdministrativeGenderCode(CcdUtils.createCE(client.getGender(), "2.16.840.1.113883.5.1"));

        TS birthTime = DatatypesFactory.eINSTANCE.createTS();
        if (client.getBirthDate() != null) {
            birthTime.setValue(CcdUtils.formatSimpleLocalDate(client.getBirthDate()));
        } else {
            birthTime.setNullFlavor(NullFlavor.NI);
        }
        patient.setBirthTime(birthTime);
        if (client.getBirthplaceAddress() != null) {
            // TODO birthplace element is specific to C-CDA CCD; exclude for HL7 CCD?
            final Birthplace birthplace = CDAFactory.eINSTANCE.createBirthplace();
            final Place place = CDAFactory.eINSTANCE.createPlace();
            final AD addr = CcdUtils.convertAddress(client.getBirthplaceAddress());
            place.setAddr(addr);
            birthplace.setPlace(place);
            patient.setBirthplace(birthplace);
        }
        if (client.getMaritalStatus() != null) {
            var maritalStatus = CcdUtils.createCE(client.getMaritalStatus(), CodeSystem.MARITAL_STATUS_CCD_R1_1.getOid());
            maritalStatus.setCodeSystem(CodeSystem.MARITAL_STATUS_CCD_R1_1.getOid()); //adjust code system
            patient.setMaritalStatusCode(maritalStatus);
        }
        if (client.getReligion() != null) {
            patient.setReligiousAffiliationCode(CcdUtils.createCE(client.getReligion(), "2.16.840.1.113883.5.1076"));
        }
        if (client.getRace() != null) {
            patient.setRaceCode(CcdUtils.createCE(client.getRace(), "2.16.840.1.113883.6.238"));
        }
        if (client.getEthnicGroup() != null) {
            patient.setEthnicGroupCode(CcdUtils.createCEFromValueSetOrTranslation(client.getEthnicGroup(),
                    ValueSetEnum.ETHNIC_GROUP, false));
        }
        List<Guardian> guardians = client.getGuardians();
        if (CollectionUtils.isNotEmpty(guardians)) {
            for (Guardian guardian : guardians) {
                org.eclipse.mdht.uml.cda.Guardian ccdGuardian = CDAFactory.eINSTANCE.createGuardian();
                if (guardian.getRelationship() != null) {
                    ccdGuardian.setCode(CcdUtils.createCE(guardian.getRelationship(), "2.16.840.1.113883.5.111"));
                }
                Person guardianPerson = guardian.getPerson();
                if (guardianPerson != null) {

                    CcdUtils.addConvertedAddresses(guardianPerson.getAddresses(), ccdGuardian.getAddrs(), false);
                    CcdUtils.addConvertedTelecoms(guardianPerson.getTelecoms(), ccdGuardian.getTelecoms(), false);

                    var person = cdaSectionEntryFactory.buildPerson(guardianPerson);
                    ccdGuardian.setGuardianPerson(person);
                    patient.getGuardians().add(ccdGuardian);
                }
            }
        }
        List<Language> languages = client.getLanguages();
        if (CollectionUtils.isNotEmpty(languages)) {
            for (Language language : languages) {
                LanguageCommunication languageCommunication = CDAFactory.eINSTANCE.createLanguageCommunication();
                CS langCode = DatatypesFactory.eINSTANCE.createCS();
                if (language.getCode() != null) {
                    langCode.setCode(language.getCode().getCode());
                } else {
                    langCode.setNullFlavor(NullFlavor.NI);
                }
                languageCommunication.setLanguageCode(langCode);
                if (language.getAbilityMode() != null) {
                    languageCommunication.setModeCode(CcdUtils.createCE(language.getAbilityMode(), "2.16.840.1.113883.5.60"));
                }
                if (language.getAbilityProficiency() != null) {
                    languageCommunication.setProficiencyLevelCode(CcdUtils.createCE(language.getAbilityProficiency(), "2.16.840.1.113883.5.61"));
                }
                if (language.getPreferenceInd() != null) {
                    BL preference = DatatypesFactory.eINSTANCE.createBL(language.getPreferenceInd());
                    languageCommunication.setPreferenceInd(preference);
                }
                patient.getLanguageCommunications().add(languageCommunication);
            }
        }
        Community ccdOrganization = client.getProviderOrganization();
        if (ccdOrganization != null) {
            org.eclipse.mdht.uml.cda.Organization organization = CDAFactory.eINSTANCE.createOrganization();
            organization.getIds().add(CcdUtils.getId(ccdOrganization.getId()));

            if (StringUtils.isNotEmpty(ccdOrganization.getProviderNpi())) {
                II npiId = CcdUtils.getNpiId(ccdOrganization.getProviderNpi());
                organization.getIds().add(npiId);
            }

            ON on = DatatypesFactory.eINSTANCE.createON();
            if (ccdOrganization.getName() != null) {
                on.addText(ccdOrganization.getName());
            } else {
                on.setNullFlavor(NullFlavor.NI);
            }
            organization.getNames().add(on);

            CcdUtils.addConvertedAddresses(ccdOrganization.getAddresses(), organization.getAddrs(), true);
            CcdUtils.addConvertedTelecoms(CareCoordinationUtils.wrapIfNonNull(ccdOrganization.getTelecom()), organization.getTelecoms(), true);

            patientRole.setProviderOrganization(organization);
        }
        patientRole.setPatient(patient);

        return patientRole;
    }

}