package org.openhealthtools.openxds.registry.patient;


import com.misyshealthcare.connect.base.demographicdata.Address;
import com.misyshealthcare.connect.base.demographicdata.PhoneNumber;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.openhealthexchange.openpixpdq.data.Patient;
import org.openhealthexchange.openpixpdq.data.PatientIdentifier;
import org.openhealthexchange.openpixpdq.data.PersonName;
import org.openhealthtools.openxds.dao.*;
import org.openhealthtools.openxds.entity.*;
import org.openhealthtools.openxds.registry.api.PatientExtended;
import org.openhealthtools.openxds.registry.api.RegistryPatientException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional(propagation = Propagation.SUPPORTS)
public class XdsRepoPatientServiceImpl implements XdsRepoPatientService {

    private final static org.apache.commons.logging.Log logger = LogFactory.getLog(XdsRepoPatientServiceImpl.class);

    private static final String DEFAULT_ADT_DATABASE = "ADT repo";
    private static final String DEFAULT_ADT_ORGANIZATION = "ADT Organization";

    private CcdCodeDao ccdCodeDao;

    private XdsRepoPatientDao xdsRepoPatientDao;

    private SourceDatabaseDao sourceDatabaseDao;

    private OrganizationDao organizationDao;

    private ResidentUpdateQueueDao residentUpdateQueueDao;

    @Override
    public Resident createPatient(Patient patient) {
        Resident resident = new Resident();

        String databaseStr = StringUtils.defaultString(patient.getPatientIds().get(0).getAssigningAuthority().getUniversalId())
                .replace("amp;", "");
        String facilityStr = patient.getPatientIds().get(0).getAssigningFacility().getUniversalId();

        final Database database = resolveDatabase(databaseStr);
        final Organization facility = resolveFacility(database, facilityStr);

        resident.setDatabase(database);
        resident.setFacility(facility);
        resident.setDatabaseId(database.getId());

        copyDemographics((PatientExtended) patient, resident);
        resident = xdsRepoPatientDao.saveAndFlush(resident);

        updateLegacyIds(resident);
        xdsRepoPatientDao.save(resident);

        residentUpdateQueueDao.pushResidentUpdate(resident.getId());

        return resident;
    }

    private Database resolveDatabase(String databaseStr) {
        final Database database = sourceDatabaseDao.findByOID(databaseStr);
        if (database != null) {
            return database;
        }
        logger.error("No database found with OID: " + databaseStr + " Record will be passed to default organization 'ADT repo'");
        return sourceDatabaseDao.findFirstByName(DEFAULT_ADT_DATABASE);

    }

    private Organization resolveFacility(Database database, String facilityStr) {
        if (DEFAULT_ADT_DATABASE.equals(database.getName())) {
            return organizationDao.findFirstByName(DEFAULT_ADT_ORGANIZATION);
        }

        if (StringUtils.isNotEmpty(facilityStr)) {
            logger.info("incoming message has assigning facility - trying to fetch");
            final Organization organization = organizationDao.findByUniversalId(facilityStr, database.getId());
            if (organization != null) {
                return organization;
            }
        }

        logger.info("Wasn't able to resolve Community so far - fetching default community");
        return organizationDao.findDefaultByDatabase(database.getId());
    }

    @Override
    public Resident updatePatient(Patient patient, Long patientRepoId) throws RegistryPatientException {
        Resident resident = xdsRepoPatientDao.findOne(patientRepoId);

        clearDemographics(resident);
        copyDemographics((PatientExtended) patient, resident);

        resident = xdsRepoPatientDao.saveAndFlush(resident);

        updateLegacyIds(resident);

        final Resident savedResident = xdsRepoPatientDao.save(resident);
        residentUpdateQueueDao.pushResidentUpdate(savedResident.getId());
        return savedResident;
    }


    private void copyDemographics(PatientExtended p, Resident resident) {
        Database database = resident.getDatabase();

        LegacyIdResolver.setLegacyId(resident);

        Person residentPerson = new Person();
        residentPerson.setLegacyTable("Resident");
        LegacyIdResolver.setLegacyId(residentPerson);
        residentPerson.setDatabase(database);

        PersonName patientName = p.getPatientName();
        if (patientName != null) {
            resident.setFirstName(patientName.getFirstName());
            resident.setLastName(patientName.getLastName());
            Name residentName = mapPatientNameToResidentName(patientName);
            residentName.setLegacyTable("Resident");
            residentName.setDatabase(database);
            residentName.setNameUse("L");
            LegacyIdResolver.setLegacyId(residentName);
            residentName.setPerson(residentPerson);
            residentPerson.getNames().add(residentName);
        }

        PersonName patientAlias = p.getPatientAlias();
        if (patientAlias != null) {
            Name patientAliasName = mapPatientNameToResidentName(patientAlias);
            patientAliasName.setLegacyTable("Resident");
            patientAliasName.setDatabase(database);
            patientAliasName.setNameUse("CL");
            LegacyIdResolver.setLegacyId(patientAliasName);
            patientAliasName.setPerson(residentPerson);
            residentPerson.getNames().add(patientAliasName);
        }

        PersonName mothersPatientName = p.getMonthersMaidenName();
        if (mothersPatientName != null) {
            Person mothersPerson = new Person();
            mothersPerson.setLegacyTable("ResidentsMother");
            LegacyIdResolver.setLegacyId(mothersPerson);
            mothersPerson.setDatabase(database);

            Name mothersName = mapPatientNameToResidentName(mothersPatientName);
            mothersName.setLegacyTable("ResidentsMother");
            mothersName.setDatabase(database);
            mothersName.setPerson(mothersPerson);
            LegacyIdResolver.setLegacyId(mothersName);
            mothersPerson.getNames().add(mothersName);
            resident.setMother(mothersPerson);
        }

        resident.setBirthOrder(p.getBirthOrder());

        resident.setBirthPlace(p.getBirthPlace());

        if (p.getBirthDateTime() != null) {
            resident.setBirthDate(p.getBirthDateTime().getTime());
        }

        if (p.getAdministrativeSex() != null) {
            switch (p.getAdministrativeSex()) {
                case FEMALE:
                    resident.setGender(ccdCodeDao.findGenderByCode("F"));
                    break;
                case MALE:
                    resident.setGender(ccdCodeDao.findGenderByCode("M"));
                    break;
                case OTHER:
                    resident.setGender(ccdCodeDao.findGenderByCode("UN"));
                    break;
                default:
                    resident.setGender(null);
            }
        }

        String ssn = p.getSsn();
        if (ssn != null) ssn = ssn.replaceAll("-", "");
        resident.setSocialSecurity(p.getSsn());
        resident.setSsnLastFourDigits(ssn == null || ssn.length() < 4 ? ssn : ssn.substring(ssn.length() - 4));


        resident.setDeathIndicator(p.isDeathIndicator());

        if (p.getDeathDate() != null) {
            resident.setDeathDate(p.getDeathDate().getTime());
        }

        resident.setCitizenship(p.getCitizenship());

        CcdCode raceCode = null;
        if (p.getRaceIdentifier() != null) raceCode = ccdCodeDao.findRaceAndEthnicityByCode(p.getRaceIdentifier());
        if ((raceCode == null) && (p.getRace() != null)) raceCode = ccdCodeDao.findRaceAndEthnicityByName(p.getRace());
        resident.setRace(raceCode);

        CcdCode maritalStatusCode = null;
        if (p.getMaritalStatusIdentifier() != null)
            maritalStatusCode = ccdCodeDao.findMaritalStatusByCode(p.getMaritalStatusIdentifier());
        if ((maritalStatusCode == null) && (p.getMaritalStatus() != null))
            maritalStatusCode = ccdCodeDao.findMaritalStatusByName(p.getMaritalStatus());
        resident.setMaritalStatus(maritalStatusCode);

        CcdCode religionCode = null;
        if (p.getReligionIdentifier() != null) religionCode = ccdCodeDao.findReligionByCode(p.getReligionIdentifier());
        if ((religionCode == null) && (p.getReligion() != null))
            religionCode = ccdCodeDao.findReligionByName(p.getReligion());
        resident.setReligion(religionCode);

        CcdCode ethnicGroupCode = null;
        if (p.getEthnicGroupIdentifier() != null)
            ethnicGroupCode = ccdCodeDao.findRaceAndEthnicityByCode(p.getEthnicGroupIdentifier());
        if ((ethnicGroupCode == null) && (p.getEthnicGroup() != null))
            ethnicGroupCode = ccdCodeDao.findRaceAndEthnicityByName(p.getEthnicGroup());
        resident.setEthnicGroup(ethnicGroupCode);

        CcdCode primaryLanguageCode = ccdCodeDao.findLanguageByCode(p.getPrimaryLanguage());
        if (p.getPrimaryLanguageIdentifier() != null)
            primaryLanguageCode = ccdCodeDao.findLanguageByCode(p.getPrimaryLanguageIdentifier());
        if ((primaryLanguageCode == null) && (p.getPrimaryLanguage() != null))
            primaryLanguageCode = ccdCodeDao.findLanguageByName(p.getPrimaryLanguage());
        if (primaryLanguageCode != null) {
            Language language = new Language();
            language.setPreferenceInd(true);
            language.setCode(primaryLanguageCode);
            language.setDatabase(database);
            language.setResident(resident);
            resident.getLanguages().add(language);
            LegacyIdResolver.setLegacyId(language);
        }

        if (p.getVeteranStatus() != null) {
            resident.setVeteran(p.getVeteranStatus());
        } else if (p.getVeteranStatusIdentifier() != null) {
            resident.setVeteran(p.getVeteranStatusIdentifier());
        }

        List<PersonAddress> personAddresses = new ArrayList<PersonAddress>(p.getAddresses().size());
        if (p.getAddresses() != null) {
            for (Address address : p.getAddresses()) {
                PersonAddress personAddress = mapPatientAddressToResidentAddress(address);
                personAddress.setPerson(residentPerson);
                personAddress.setLegacyTable("Resident");
                personAddress.setDatabase(database);
                LegacyIdResolver.setLegacyId(personAddress);
                personAddresses.add(personAddress);
            }
        }
        residentPerson.setAddresses(personAddresses);

        int syncQualifier = 0;

        List<PersonTelecom> personTelecoms = new ArrayList<PersonTelecom>(p.getPhoneNumbers().size());
        if (p.getPhoneNumbers() != null) {
            for (PhoneNumber phoneNumber : p.getPhoneNumbers()) {
                PersonTelecom personTelecom = mapToPersonTelecom(phoneNumber);
                personTelecom.setPerson(residentPerson);
                personTelecom.setLegacyTable("Resident");
                personTelecom.setDatabase(database);
                personTelecom.setSyncQualifier(++syncQualifier);
                LegacyIdResolver.setLegacyId(personTelecom);
                personTelecoms.add(personTelecom);
/*
                if (phoneNumber.getEmail() != null) {
                    PersonTelecom personEmail = new PersonTelecom();
                    personEmail.setPerson(residentPerson);
                    personEmail.setLegacyTable("Resident");
                    personEmail.setDatabase(database);
                    LegacyIdResolver.setLegacyId(personEmail);
                    personEmail.setValue(phoneNumber.getEmail());
                    personEmail.setUseCode("EMAIL");
                    personTelecoms.add(personEmail);
                }*/
            }
        }

        if (p.getEmails() != null) {
            for (PhoneNumber phoneNumber : p.getEmails()) {
                PersonTelecom personEmail = new PersonTelecom();
                personEmail.setPerson(residentPerson);
                personEmail.setLegacyTable("Resident");
                personEmail.setDatabase(database);
                LegacyIdResolver.setLegacyId(personEmail);
                personEmail.setSyncQualifier(++syncQualifier);
                personEmail.setValue(phoneNumber.getEmail());
                personEmail.setUseCode("EMAIL");
                personTelecoms.add(personEmail);
            }
        }

        residentPerson.setAddresses(personAddresses);
        residentPerson.setTelecoms(personTelecoms);
        resident.setPerson(residentPerson);

        PatientIdentifier patientAccountNumber = p.getPatientAccountNumber();
        if (patientAccountNumber != null && patientAccountNumber.getId() != null &&
                patientAccountNumber.getAssigningAuthority() != null && patientAccountNumber.getAssigningAuthority().getUniversalId() != null) {
            resident.setPatientAccountNumber(PersonIdentifier.createFromPatientIdentifier(patientAccountNumber));
        }

        org.openhealthexchange.openpixpdq.data.PersonIdentifier monthersId = p.getMonthersId();
        if (monthersId != null && monthersId.getId() != null &&
                monthersId.getAssigningAuthority() != null && monthersId.getAssigningAuthority().getUniversalId() != null) {
            resident.setMothersId(PersonIdentifier.createFromPatientIdentifier(monthersId));
        }

        // DriversLicense driversLicense;
        // List<Visit> visits;
    }


    private Resident clearDemographics(Resident resident) {
        resident.setBirthOrder(null);
        resident.setBirthPlace(null);
        resident.setBirthDate(null);
        resident.setGender(null);
        resident.setSocialSecurity(null);
        resident.setSsnLastFourDigits(null);
        resident.setCitizenship(null);
        resident.setDeathIndicator(false);
        resident.setRace(null);
        resident.setMaritalStatus(null);
        resident.setReligion(null);
        resident.setEthnicGroup(null);
        resident.getLanguages().clear();
        resident.setPatientAccountNumber(null);
        resident.setMothersId(null);
        Person residentPerson = resident.getPerson();
        if (residentPerson != null) {
            residentPerson.getNames().clear();
            residentPerson.getAddresses().clear();
            residentPerson.getTelecoms().clear();
            resident.setPerson(null);
        }
        Person mothersPerson = resident.getMother();
        if (mothersPerson != null) {
            mothersPerson.getNames().clear();
            resident.setMother(null);
        }
        return resident;
    }

    private Name mapPatientNameToResidentName(PersonName patientName) {
        Name residentName = new Name();

        residentName.setDegree(patientName.getDegree());
        residentName.setFamily(patientName.getLastName());
        residentName.setFamilyNormalized(normalizeName(patientName.getLastName()));
        residentName.setGiven(patientName.getFirstName());
        residentName.setGivenNormalized(normalizeName(patientName.getFirstName()));
        residentName.setMiddle(patientName.getSecondName());
        residentName.setSuffix(patientName.getSuffix());
        residentName.setPrefix(patientName.getPrefix());
        residentName.setNameRepresentationCode(patientName.getNameRepresentationCode());

        return residentName;
    }

    private static String normalizeName(String str) {
        return str == null ? str : str.toLowerCase().replaceAll("[' \\-]", "");
    }

    private PersonAddress mapPatientAddressToResidentAddress(Address address) {
        PersonAddress residentAddress = new PersonAddress();

        residentAddress.setCity(address.getAddCity());
        residentAddress.setCountry(address.getAddCountry());
        residentAddress.setStreetAddress(address.getAddLine1() + (address.getAddLine2() == null ? "" : address.getAddLine2()));
        residentAddress.setPostalCode(address.getAddZip());
        residentAddress.setState(address.getAddState());
        if (address.getAddType() != null)
            residentAddress.setPostalAddressUse(address.getAddType().getValue());

        return residentAddress;
    }

    private PersonTelecom mapToPersonTelecom(PhoneNumber phoneNumber) {
        PersonTelecom t = new PersonTelecom();

        StringBuilder number = new StringBuilder();
        number.append(phoneNumber.getExtension());
        number.append(phoneNumber.getCountryCode());
        number.append(phoneNumber.getAreaCode());
        number.append(phoneNumber.getNumber());
        t.setValue(number.toString().replaceAll("null", ""));

        if (phoneNumber.getType() != null)
            t.setUseCode(phoneNumber.getType().getCDAValue());

        return t;
    }

    private void updateLegacyIds(Resident resident) {
        LegacyIdResolver.setLegacyId(resident);

        Person person = resident.getPerson();
        LegacyIdResolver.setLegacyId(person);

        for (PersonAddress personAddress : person.getAddresses()) {
            LegacyIdResolver.setLegacyId(personAddress);
        }

        for (PersonTelecom telecom : person.getTelecoms()) {
            LegacyIdResolver.setLegacyId(telecom);
        }

        for (Name name : person.getNames()) {
            LegacyIdResolver.setLegacyId(name);
        }

        for (Language language : resident.getLanguages()) {
            LegacyIdResolver.setLegacyId(language);
        }

        Person mother = resident.getMother();
        if (mother != null) {
            LegacyIdResolver.setLegacyId(mother);
            for (Name name : mother.getNames()) {
                LegacyIdResolver.setLegacyId(name);
            }
        }
    }

    public void setCcdCodeDao(CcdCodeDao ccdCodeDao) {
        this.ccdCodeDao = ccdCodeDao;
    }

    public void setXdsRepoPatientDao(XdsRepoPatientDao xdsRepoPatientDao) {
        this.xdsRepoPatientDao = xdsRepoPatientDao;
    }

    public void setSourceDatabaseDao(SourceDatabaseDao sourceDatabaseDao) {
        this.sourceDatabaseDao = sourceDatabaseDao;
    }

    public void setOrganizationDao(OrganizationDao organizationDao) {
        this.organizationDao = organizationDao;
    }

    public void setResidentUpdateQueueDao(ResidentUpdateQueueDao residentUpdateQueueDao) {
        this.residentUpdateQueueDao = residentUpdateQueueDao;
    }
}
