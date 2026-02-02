package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.Guardian;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.PersonFactory;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.templates.RequiredTemplateFactory;
import org.apache.commons.collections.CollectionUtils;
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
 * @see Resident
 * @see Guardian
 * @see Person
 * @see PersonAddress
 * @see PersonTelecom
 * @see Language
 * @see Name
 */
@Component
public class RecordTargetFactoryImpl extends RequiredTemplateFactory implements RecordTargetFactory {

    @Autowired
    private CcdCodeFactory ccdCodeFactory;

    private static final Logger logger = LoggerFactory.getLogger(RecordTargetFactoryImpl.class);
    private static final String LEGACY_TABLE = "Resident_NWHIN";

    @Override
    public Resident parseSection(Organization organization, PatientRole ccdPatientRole) {
        if (!CcdParseUtils.hasContent(ccdPatientRole)) {
            return null;
        }
        checkNotNull(organization);

        final Resident result = new Resident();
        result.setFacility(organization);
        result.setLegacyTable(LEGACY_TABLE);
        result.setDatabase(organization.getDatabase());
        result.setDatabaseId(organization.getDatabaseId());

        final Patient patient = ccdPatientRole.getPatient();

        // Note: Real resident legacy ID (from ResidentDTO) will overwrite this value later in CcdMediator service
        result.setLegacyId(CcdParseUtils.getFirstIdExtensionStr(ccdPatientRole.getIds()));

        // TODO used default organization
        final Organization providerOrganization = CcdTransform.toOrganization(ccdPatientRole.getProviderOrganization(),
                organization.getDatabase(), LEGACY_TABLE);
        result.setProviderOrganization(organization);
        if (patient != null) {
            result.setBirthDate(CcdParseUtils.convertTsToDate(patient.getBirthTime()));
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
                    final Person guardianPerson = CcdParseUtils.createPerson(ccdGuardian, organization.getDatabase(),
                            LEGACY_TABLE);
                    if (guardianPerson != null) {
                        Guardian guardian = new Guardian();
                        guardian.setDatabase(result.getDatabase());
                        guardian.setDatabaseId(result.getDatabaseId());
                        guardian.setResident(result);
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
                    language.setResident(result);
                    language.setDatabase(result.getDatabase());
                    language.setDatabaseId(result.getDatabaseId());
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

        final Person person = PersonFactory.parse(ccdPatientRole, organization.getDatabase(), LEGACY_TABLE);
        result.setPerson(person);

        return result;
    }

    private static BirthplaceAddress convert(Birthplace birthplace, Resident resident) {
        if (!CcdParseUtils.hasContent(birthplace) || !CcdParseUtils.hasContent(birthplace.getPlace())) {
            return null;
        }
        return CcdParseUtils.createAddress(birthplace.getPlace().getAddr(), resident);
    }

    @Override
    public PatientRole buildTemplateInstance(Resident resident) {
        checkNotNull(resident);

        final PatientRole patientRole = CDAFactory.eINSTANCE.createPatientRole();

        // create ccd template
        patientRole.getIds().add(CcdUtils.getId(resident.getId()));

        final Patient patient = CDAFactory.eINSTANCE.createPatient();
        final Person ccdPerson = resident.getPerson();
        if (ccdPerson != null) {
            if (ccdPerson.getNames() != null) {
                for (Name name : resident.getPerson().getNames()) {
                    if ("L".equals(name.getNameUse())) {
                        CcdUtils.addConvertedName(patient.getNames(), name);
                    }
                }
            } else {
                patient.getNames().add(CcdUtils.getNullName());
            }
            if (ccdPerson.getAddresses() != null) {
                for (PersonAddress address : ccdPerson.getAddresses()) {
                    CcdUtils.addConvertedAddress(patientRole.getAddrs(), address);
                }
            } else {
                patientRole.getAddrs().add(CcdUtils.getNullAddress());
            }
            if (ccdPerson.getTelecoms() != null) {
                for (PersonTelecom telecom : ccdPerson.getTelecoms()) {
                    CcdUtils.addConvertedTelecom(patientRole.getTelecoms(), telecom);
                }
            } else {
                patientRole.getTelecoms().add(CcdUtils.getNullTelecom());
            }
        }

        patient.setAdministrativeGenderCode(CcdUtils.createCE(resident.getGender(),"2.16.840.1.113883.5.1"));

        TS birthTime = DatatypesFactory.eINSTANCE.createTS();
        if (resident.getBirthDate() != null) {
            birthTime.setValue(CcdUtils.formatSimpleDate(resident.getBirthDate()));
        } else {
            birthTime.setNullFlavor(NullFlavor.NI);
        }
        patient.setBirthTime(birthTime);
        if (resident.getBirthplaceAddress() != null) {
            // TODO birthplace element is specific to C-CDA CCD; exclude for HL7 CCD?
            final Birthplace birthplace = CDAFactory.eINSTANCE.createBirthplace();
            final Place place = CDAFactory.eINSTANCE.createPlace();
            final AD addr = CcdUtils.convertAddress(resident.getBirthplaceAddress());
            place.setAddr(addr);
            birthplace.setPlace(place);
            patient.setBirthplace(birthplace);
        }
        if (resident.getMaritalStatus() != null) {
            patient.setMaritalStatusCode(CcdUtils.createCE(resident.getMaritalStatus(), "2.16.840.1.113883.5.2"));
        }
        if (resident.getReligion() != null) {
            patient.setReligiousAffiliationCode(CcdUtils.createCE(resident.getReligion(), "2.16.840.1.113883.5.1076"));
        }
        if (resident.getRace() != null) {
            patient.setRaceCode(CcdUtils.createCE(resident.getRace(), "2.16.840.1.113883.6.238"));
        }
        if (resident.getEthnicGroup() != null) {
            patient.setEthnicGroupCode(CcdUtils.createCE(resident.getEthnicGroup(), "2.16.840.1.113883.6.238"));
        }
        List<Guardian> guardians = resident.getGuardians();
        if (guardians != null) {
            for (Guardian guardian : guardians) {
                org.eclipse.mdht.uml.cda.Guardian ccdGuardian = CDAFactory.eINSTANCE.createGuardian();
                if (guardian.getRelationship() != null) {
                    ccdGuardian.setCode(CcdUtils.createCE(guardian.getRelationship(),"2.16.840.1.113883.5.111"));
                }
                Person guardianPerson = guardian.getPerson();
                if (guardianPerson != null) {
                    if (guardianPerson.getAddresses() != null) {
                        for (PersonAddress address : guardianPerson.getAddresses()) {
                            CcdUtils.addConvertedAddress(ccdGuardian.getAddrs(), address);
                        }
                    }
                    if (guardianPerson.getTelecoms() != null) {
                        for (PersonTelecom telecom : guardianPerson.getTelecoms()) {
                            CcdUtils.addConvertedTelecom(ccdGuardian.getTelecoms(), telecom);
                        }
                    }
                    org.eclipse.mdht.uml.cda.Person person = CDAFactory.eINSTANCE.createPerson();
                    if (guardianPerson.getNames() != null) {
                        for (Name name : guardianPerson.getNames()) {
                            CcdUtils.addConvertedName(person.getNames(), name);
                        }
                    } else {
                        person.getNames().add(CcdUtils.getNullName());
                    }
                    ccdGuardian.setGuardianPerson(person);
                    patient.getGuardians().add(ccdGuardian);
                }
            }
        }
        List<Language> languages = resident.getLanguages();
        if (languages != null) {
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
                    languageCommunication.setModeCode(CcdUtils.createCE(language.getAbilityMode(),"2.16.840.1.113883.5.60"));
                }
                if (language.getAbilityProficiency() != null) {
                    languageCommunication.setProficiencyLevelCode(CcdUtils.createCE(language.getAbilityProficiency(),"2.16.840.1.113883.5.61"));
                }
                if (language.getPreferenceInd() != null) {
                    BL preference = DatatypesFactory.eINSTANCE.createBL(language.getPreferenceInd());
                    languageCommunication.setPreferenceInd(preference);
                }
                patient.getLanguageCommunications().add(languageCommunication);
            }
        }
        Organization ccdOrganization = resident.getProviderOrganization();
        if (ccdOrganization != null) {
            org.eclipse.mdht.uml.cda.Organization organization = CDAFactory.eINSTANCE.createOrganization();
            organization.getIds().add(CcdUtils.getId(ccdOrganization.getId()));

            if(ccdOrganization.getProviderNpi() != null) {
                II npiId = DatatypesFactory.eINSTANCE.createII();
                npiId.setRoot("2.16.840.1.113883.4.6");
                npiId.setExtension(ccdOrganization.getProviderNpi());
                organization.getIds().add(npiId);
            }

            ON on = DatatypesFactory.eINSTANCE.createON();
            if (ccdOrganization.getName() != null) {
                on.addText(ccdOrganization.getName());
            } else {
                on.setNullFlavor(NullFlavor.NI);
            }
            organization.getNames().add(on);
            if (ccdOrganization.getAddresses() != null) {
                for (OrganizationAddress address : ccdOrganization.getAddresses()) {
                    CcdUtils.addConvertedAddress(organization.getAddrs(), address);
                }
            } else {
                organization.getAddrs().add(CcdUtils.getNullAddress());
            }
            if (ccdOrganization.getTelecom() != null) {
                CcdUtils.addConvertedTelecom(organization.getTelecoms(), ccdOrganization.getTelecom());
            } else {
                organization.getTelecoms().add(CcdUtils.getNullTelecom());
            }
            patientRole.setProviderOrganization(organization);
        }
        patientRole.setPatient(patient);

        return patientRole;
    }

}