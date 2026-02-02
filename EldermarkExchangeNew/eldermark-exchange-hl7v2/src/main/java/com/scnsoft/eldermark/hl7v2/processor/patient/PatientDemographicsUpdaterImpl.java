package com.scnsoft.eldermark.hl7v2.processor.patient;

import com.scnsoft.eldermark.dao.CcdCodeDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.document.facesheet.Language;
import com.scnsoft.eldermark.hl7v2.model.AddressDemographics;
import com.scnsoft.eldermark.hl7v2.model.PersonIdentifier;
import com.scnsoft.eldermark.hl7v2.model.PersonName;
import com.scnsoft.eldermark.hl7v2.model.PhoneNumber;
import com.scnsoft.eldermark.hl7v2.processor.patient.demographics.HL7v2PatientDemographics;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.*;

@Service
public class PatientDemographicsUpdaterImpl implements PatientDemographicsUpdater {

    @Autowired
    private CcdCodeDao ccdCodeDao;

    //todo delete after everything tested
//    private Client clearDemographics(Client resident) {
//        resident.setBirthOrder(null);
//        resident.setBirthPlace(null);
//        resident.setBirthDate(null);
//        resident.setGender(null);
//        resident.setSocialSecurity(null);
//        resident.setSsnLastFourDigits(null);
//        resident.setCitizenship(null);
//        resident.setDeathIndicator(false);
//        resident.setRace(null);
//        resident.setMaritalStatus(null);
//        resident.setReligion(null);
//        resident.setEthnicGroup(null);
//        resident.getLanguages().clear();
//        resident.setPatientAccountNumber(null);
//        resident.setMothersId(null);
//        var residentPerson = resident.getPerson();
//        if (residentPerson != null) {
//            residentPerson.getNames().clear();
//            residentPerson.getAddresses().clear();
//            residentPerson.getTelecoms().clear();
//            resident.setPerson(null);
//        }
//        Person mothersPerson = resident.getMother();
//        if (mothersPerson != null) {
//            mothersPerson.getNames().clear();
//            resident.setMother(null);
//        }
//        return resident;
//    }

    @Override
    @Transactional
    public Client updateDemographics(Client patient, HL7v2PatientDemographics patientDemographics) {
        var database = patient.getOrganization();

        Person residentPerson = patient.getPerson();
        if (residentPerson == null) {
            residentPerson = new Person();
            residentPerson.setLegacyTable("Resident");
            LegacyIdResolver.setLegacyId(residentPerson);
            residentPerson.setOrganization(database);
            residentPerson.setOrganizationId(database.getId());
            patient.setPerson(residentPerson);
        }

        var patientName = patientDemographics.getPatientName();
        if (patientName != null) {
            patient.setFirstName(patientName.getFirstName());
            patient.setLastName(patientName.getLastName());

            createOrUpdatePersonName(residentPerson, patientName, "L", "Resident");
        }

        var patientAlias = patientDemographics.getPatientAliasName();
        if (patientAlias != null) {
            createOrUpdatePersonName(residentPerson, patientName, "CL", "Resident");
        }

        var mothersPatientName = patientDemographics.getMotherMaidenName();
        if (mothersPatientName != null) {
            Person mothersPerson = patient.getMother();

            if (mothersPerson == null) {
                mothersPerson = new Person();

                mothersPerson.setLegacyTable("ResidentsMother");
                LegacyIdResolver.setLegacyId(mothersPerson);
                mothersPerson.setOrganization(database);
                mothersPerson.setOrganizationId(database.getId());
                patient.setMother(mothersPerson);
            }

            createOrUpdatePersonName(mothersPerson, mothersPatientName, "L", "ResidentsMother");
        } else {
            patient.setMother(null);
        }

        if (patientDemographics.getAddressList() != null) {
            var personAddresses = residentPerson.getAddresses();
            if (personAddresses == null) {
                personAddresses = new ArrayList<>(patientDemographics.getAddressList().size());
                residentPerson.setAddresses(personAddresses);
            }

            for (int i = 0; i < patientDemographics.getAddressList().size(); i++) {
                var address = patientDemographics.getAddressList().get(i);
                createOrUpdatePersonAddress(residentPerson, address, i, "Resident");
            }
        } else {
            Optional.ofNullable(residentPerson.getAddresses()).ifPresent(Collection::clear);
        }

        var size = CollectionUtils.emptyIfNull(patientDemographics.getPhoneList()).size();
        if (size > 0) {
            var personTelecoms = residentPerson.getTelecoms();
            if (personTelecoms == null) {
                personTelecoms = new ArrayList<>(size);
                residentPerson.setTelecoms(personTelecoms);
            }

            if (patientDemographics.getPhoneList() != null) {
                updatePhoneTelecoms(personTelecoms, patientDemographics.getPhoneList(), residentPerson);
            }
        } else {
            if (residentPerson.getTelecoms() == null) {
                residentPerson.setTelecoms(List.of());
            } else {
                residentPerson.getTelecoms().clear();
            }
        }

        patient.setBirthOrder(patientDemographics.getBirthOrder());
        patient.setBirthPlace(patientDemographics.getBirthPlace());

        patient.setBirthDate(patientDemographics.getBirthDate());

        if (patientDemographics.getSexType() != null) {
            switch (patientDemographics.getSexType()) {
                case FEMALE:
                    patient.setGender(ccdCodeDao.getCcdCode("F", CodeSystem.ADMINISTRATIVE_GENDER.getOid()));
                    break;
                case MALE:
                    patient.setGender(ccdCodeDao.getCcdCode("M", CodeSystem.ADMINISTRATIVE_GENDER.getOid()));
                    break;
                case OTHER:
                    patient.setGender(ccdCodeDao.getCcdCode("UN", CodeSystem.ADMINISTRATIVE_GENDER.getOid()));
                    break;
                default:
                    patient.setGender(null);
            }
        } else {
            patient.setGender(null);
        }

        var ssn = CareCoordinationUtils.normalizePhone(patientDemographics.getSsn());
        patient.setSocialSecurity(ssn);
        patient.setSsnLastFourDigits(StringUtils.right(ssn, 4));

        patient.setDeathIndicator(patientDemographics.getDeathIndicator());

        if (patientDemographics.getDeathDate() != null) {
            patient.setDeathDate(patientDemographics.getDeathDate().atZone(ZoneId.systemDefault()).toInstant());
        } else {
            patient.setDeathDate(null);
        }

        patient.setCitizenship(patientDemographics.getCitizenShip());

        patient.setRace(resolveCcdCode(
                patientDemographics.getRaceIdentifier(),
                patientDemographics.getRace(),
                CodeSystem.RACE_AND_ETHNICITY_CDC)
        );

        patient.setMaritalStatus(resolveCcdCode(
                patientDemographics.getMaritalStatusIdentifier(),
                patientDemographics.getMaritalStatus(),
                CodeSystem.MARITAL_STATUS
        ));

        patient.setReligion(resolveCcdCode(
                patientDemographics.getReligionIdentifier(),
                patientDemographics.getReligion(),
                CodeSystem.RELIGIOUS_AFFILIATION
        ));

        patient.setEthnicGroup(resolveCcdCode(
                patientDemographics.getEthnicGroupIdentifier(),
                patientDemographics.getEthnicGroup(),
                CodeSystem.RACE_AND_ETHNICITY_CDC)
        );

        Optional.ofNullable(patientDemographics.getPrimaryLanguage())
                .map(pl -> ccdCodeDao.findFirstByCodeAndCodeSystem(
                        pl,
                        CodeSystem.LANGUAGE.getOid()
                ))
                .or(() -> Optional.ofNullable(resolveCcdCode(
                        patientDemographics.getPrimaryLanguageIdentifier(),
                        patientDemographics.getPrimaryLanguage(),
                        CodeSystem.LANGUAGE
                )))
                .ifPresentOrElse(primaryLanguageCode -> {
                            var languages = patient.getLanguages();
                            if (languages == null) {
                                languages = new ArrayList<>();
                                patient.setLanguages(languages);
                            }

                            var language = languages.size() > 0 ? languages.get(0) : null;

                            if (language == null) {
                                language = new Language();
                                patient.getLanguages().add(language);
                                LegacyIdResolver.setLegacyId(language);
                            }

                            //or update instead
                            language.setPreferenceInd(true);
                            language.setCode(primaryLanguageCode);
                            language.setOrganization(database);
                            language.setOrganizationId(database.getId());
                            language.setClient(patient);
                        },
                        () -> Optional.ofNullable(patient.getLanguages()).ifPresent(Collection::clear)
                );

        patient.setVeteran(StringUtils.firstNonEmpty(
                patientDemographics.getVeteranMilitaryStatus(),
                patientDemographics.getVeteranMilitaryStatusIdentifier())
        );

        patient.setPatientAccountNumber(
                Optional.ofNullable(patientDemographics.getPatientAccountNumber())
                        .filter(this::isNotEmpty)
                        .map(pan -> PersonIdentifier.createOrUpdateMPIFromPersonIdentifier(patient.getPatientAccountNumber(), pan))
                        .orElse(null)
        );

        patient.setMothersId(
                Optional.ofNullable(patientDemographics.getMothersId())
                        .filter(this::isNotEmpty)
                        .map(mi -> PersonIdentifier.createOrUpdateMPIFromPersonIdentifier(patient.getMothersId(), mi))
                        .orElse(null)
        );

        return patient;

        // DriversLicense driversLicense;
        // List<Visit> visits;
    }

    private void createOrUpdatePersonAddress(Person residentPerson, AddressDemographics address, int index, String legacyTable) {
        var patientAddresses = residentPerson.getAddresses();

        PersonAddress personAddress;
        if (index >= patientAddresses.size()) {
            personAddress = new PersonAddress();
            personAddress.setPerson(residentPerson);
            personAddress.setLegacyTable(legacyTable);
            personAddress.setOrganization(residentPerson.getOrganization());
            personAddress.setOrganizationId(residentPerson.getOrganizationId());
            LegacyIdResolver.setLegacyId(personAddress);
            patientAddresses.add(personAddress);
        } else {
            personAddress = patientAddresses.get(index);
        }

        personAddress.setCity(address.getAddCity());
        personAddress.setCountry(address.getAddCountry());
        personAddress.setStreetAddress(CareCoordinationUtils.concat(" ", address.getAddLine1(), address.getAddLine2()));
        personAddress.setPostalCode(address.getAddZip());
        personAddress.setState(address.getAddState());
        personAddress.setPostalAddressUse(
                Optional.ofNullable(address.getAddType())
                        .map(AddressDemographics.AddressType::getValue).orElse(null)
        );
    }

    private void createOrUpdatePersonName(Person person, PersonName personName, String nameUse, String legacyTable) {
        if (person.getNames() == null) {
            person.setNames(new ArrayList<>());
        }
        person.getNames().stream()
                .filter(item -> nameUse.equals(item.getNameUse()))
                .findFirst()
                .ifPresentOrElse(
                        name -> fillName(name, personName),
                        () -> {
                            Name name = new Name();
                            fillName(name, personName);

                            name.setLegacyTable(legacyTable);
                            name.setOrganization(person.getOrganization());
                            name.setOrganizationId(person.getOrganization().getId());
                            name.setNameUse(nameUse);
                            LegacyIdResolver.setLegacyId(name);
                            name.setPerson(person);
                            person.getNames().add(name);
                        });
    }


    private void fillName(Name residentName, PersonName patientName) {
        residentName.setDegree(patientName.getDegree());
        residentName.setFamily(patientName.getLastName());
        residentName.setGiven(patientName.getFirstName());
        residentName.setMiddle(patientName.getSecondName());
        residentName.setSuffix(patientName.getSuffix());
        residentName.setPrefix(patientName.getPrefix());
        residentName.setNameRepresentationCode(patientName.getNameRepresentationCode());
    }

    private CcdCode resolveCcdCode(String code, String name, CodeSystem codeSystem) {
        CcdCode raceCode = null;
        if (code != null) {
            raceCode = ccdCodeDao.findFirstByCodeAndCodeSystem(code, codeSystem.getOid());
        }
        if (raceCode == null && name != null) {
            raceCode = ccdCodeDao.findFirstByDisplayNameAndCodeSystem(name, codeSystem.getOid());
        }
        return raceCode;
    }

    private boolean isNotEmpty(PersonIdentifier personIdentifier) {
        return personIdentifier != null &&
                personIdentifier.getId() != null &&
                personIdentifier.getAssigningAuthority() != null &&
                personIdentifier.getAssigningAuthority().getUniversalId() != null;
    }


    private void updatePhoneTelecoms(List<PersonTelecom> personTelecoms, List<PhoneNumber> phoneList, Person residentPerson) {
        var oldTelecoms = new ArrayList<>(personTelecoms);

        //there is unique constraint (person, code) in DB
        var updatedTelecomTypes = EnumSet.noneOf(PersonTelecomCode.class);

        for (var phoneNumber : phoneList) {
            var code = Optional.ofNullable(phoneNumber.getType())
                    .map(PhoneNumber.PhoneType::getCDAValue)
                    .flatMap(PersonTelecomCode::fromName)
                    .orElse(PersonTelecomCode.UN);

            if (updatedTelecomTypes.contains(code)) {
                //use only first entry if there are multiple phones with the same code
                continue;
            }

            var value = CareCoordinationUtils.concat("", phoneNumber.getExtension(),
                    phoneNumber.getCountryCode(),
                    phoneNumber.getAreaCode(),
                    phoneNumber.getNumber());

            if (StringUtils.isEmpty(value)) {
                //don't save empty phones
                continue;
            }

            PersonTelecom personTelecom = oldTelecoms.stream()
                    .filter(t -> code.getCode() == t.getSyncQualifier())
                    .findFirst()
                    .orElseGet(() -> {
                        var t = new PersonTelecom();
                        t.setUseCode(code.name());
                        t.setSyncQualifier(code.getCode());

                        t.setPerson(residentPerson);
                        t.setLegacyTable("Resident");
                        t.setOrganization(residentPerson.getOrganization());
                        t.setOrganizationId(residentPerson.getOrganization().getId());
                        LegacyIdResolver.setLegacyId(t);
                        return t;
                    });

            personTelecom.setValue(value);
            updatedTelecomTypes.add(code);
            personTelecoms.add(personTelecom);
        }
    }
}
