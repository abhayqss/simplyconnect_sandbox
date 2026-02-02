package com.scnsoft.eldermark.consana.sync.server.services.converter;

import com.scnsoft.eldermark.consana.sync.server.model.entity.Person;
import com.scnsoft.eldermark.consana.sync.server.model.entity.*;
import com.scnsoft.eldermark.consana.sync.server.model.enums.PersonTelecomCode;
import com.scnsoft.eldermark.consana.sync.server.services.gateway.ConsanaGateway;
import com.scnsoft.eldermark.consana.sync.server.utils.FhirConversionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.Address;
import org.hl7.fhir.instance.model.Encounter;
import org.hl7.fhir.instance.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional(propagation = Propagation.SUPPORTS, noRollbackFor = Exception.class)
public class ConsanaEncounterToPersonConverter {

    private static final String LEGACY_ID = "FHIR";
    private static final String LEGACY_TABLE = "Encounter_NWHIN";

    @Autowired
    private ConsanaGateway consanaGateway;

    public Person convert(Encounter source, Resident resident, Person target) {
        if (target == null) {
            target = new Person();
        }
        target.setDatabase(resident.getDatabase());
        target.setLegacyTable(LEGACY_TABLE);
        target.setLegacyId(LEGACY_ID);
        addData(source, target);
        return target;
    }

    private void addData(Encounter source, Person person) {
        if (!source.hasParticipant()) {
            return;
        }

        var names = new ArrayList<Name>();
        var telecoms = new ArrayList<PersonTelecom>();
        var addresses = new ArrayList<PersonAddress>();
        source.getParticipant().stream()
                .filter(Encounter.EncounterParticipantComponent::hasIndividual)
                .map(Encounter.EncounterParticipantComponent::getIndividual)
                .filter(Reference::hasReferenceElement)
                .map(Reference::getReferenceElement)
                .forEach(re -> {
                    var individualPerson = consanaGateway.getIndividualPerson(re);
                    Optional.ofNullable(convertName(individualPerson, person,
                            FhirConversionUtils.getOrCreateCollectionElement(person.getNames(), names.size(), Name::new))).ifPresent(names::add);
                    telecoms.addAll(convertPersonTelecoms(individualPerson, person, person.getTelecoms(), telecoms));
                    addresses.addAll(convertPersonAddresses(individualPerson, person, person.getAddresses(), addresses));
                });
        person.setNames(FhirConversionUtils.updateCollection(names, person.getNames()));
        person.setTelecoms(FhirConversionUtils.updateCollection(telecoms, person.getTelecoms()));
        person.setAddresses(FhirConversionUtils.updateCollection(addresses, person.getAddresses()));
    }

    private Name convertName(IndividualPerson individualPerson, Person person, Name target) {
        if (!individualPerson.hasName()) {
            return null;
        }
        var humanName = individualPerson.getName();
        target.setNameUse(getNameUseCode(humanName));
        target.setDatabase(person.getDatabase());
        target.setPerson(person);
        target.setFamily(humanName.hasFamily() ? joinStringTypes(humanName.getFamily()) : null);
        target.setGiven(humanName.hasGiven() ? joinStringTypes(humanName.getGiven()) : null);
        target.setPrefix(humanName.hasPrefix() ? joinStringTypes(humanName.getPrefix()) : null);
        target.setSuffix(humanName.hasPrefix() ? joinStringTypes(humanName.getSuffix()) : null);
        target.setLegacyId(LEGACY_ID);
        target.setLegacyTable(LEGACY_TABLE);
        return target;
    }

    private String getNameUseCode(HumanName humanName) {
        if (HumanName.NameUse.USUAL == humanName.getUse() || HumanName.NameUse.OFFICIAL == humanName.getUse()
                || HumanName.NameUse.MAIDEN == humanName.getUse()) {
            return "L";
        }
        if (HumanName.NameUse.TEMP == humanName.getUse() || HumanName.NameUse.ANONYMOUS == humanName.getUse()
                || HumanName.NameUse.OLD == humanName.getUse()) {
            return "ASGN";
        }
        if (HumanName.NameUse.NICKNAME == humanName.getUse()) {
            return "P";
        }
        return null;
    }

    private List<PersonTelecom> convertPersonTelecoms(IndividualPerson individualPerson, Person person, List<PersonTelecom> source, List<PersonTelecom> target) {
        if (!individualPerson.hasTelecom()) {
            return target;
        }
        individualPerson.getTelecom()
                .forEach(t -> Optional.ofNullable(convertPersonTelecom(t, person, FhirConversionUtils.getOrCreateCollectionElement(source, target.size(), PersonTelecom::new)))
                        .ifPresent(target::add));
        return target;
    }

    private PersonTelecom convertPersonTelecom(ContactPoint source, Person person, PersonTelecom target) {
        var personTelecomCode = getPersonTelecomCode(source);
        if (personTelecomCode == null) {
            return null;
        }
        target.setUseCode(personTelecomCode.name());
        target.setSyncQualifier(personTelecomCode.getCode());
        target.setDatabase(person.getDatabase());
        target.setPerson(person);
        target.setLegacyId(LEGACY_ID);
        target.setLegacyTable(LEGACY_TABLE);
        target.setValue(source.hasValue() ? source.getValue() : null);
        return target;
    }

    private PersonTelecomCode getPersonTelecomCode(ContactPoint point) {
        if (!point.hasSystem()) {
            return null;
        }
        var system = point.getSystem();
        var use = point.getUse();
        if (ContactPoint.ContactPointSystem.PHONE == system) {
            if (ContactPoint.ContactPointUse.HOME == use) {
                return PersonTelecomCode.HP;
            }
            if (ContactPoint.ContactPointUse.WORK == use) {
                return PersonTelecomCode.WP;
            }
        }
        if (ContactPoint.ContactPointSystem.EMAIL == system) {
            return PersonTelecomCode.EMAIL;
        }
        if (ContactPoint.ContactPointSystem.FAX == system) {
            return PersonTelecomCode.FAX;
        }
        return null;
    }

    private List<PersonAddress> convertPersonAddresses(IndividualPerson individualPerson, Person person, List<PersonAddress> source, List<PersonAddress> target) {
        if (!individualPerson.hasAddress()) {
            return target;
        }
        individualPerson.getAddress().forEach(a ->
                Optional.ofNullable(convertPersonAddress(a, person, FhirConversionUtils.getOrCreateCollectionElement(source, target.size(), PersonAddress::new)))
                        .ifPresent(target::add));
        return target;
    }

    private PersonAddress convertPersonAddress(Address address, Person person, PersonAddress target) {
        if (!address.hasCity() && !address.hasCountry() && !address.hasState() && !address.hasPostalCode() && !address.hasLine()) {
            return null;
        }
        target.setDatabase(person.getDatabase());
        target.setPerson(person);
        target.setLegacyId(LEGACY_ID);
        target.setLegacyTable(LEGACY_TABLE);
        target.setCity(address.getCity());
        target.setCountry(address.getCountry());
        target.setState(address.getState());
        target.setPostalCode(address.getPostalCode());
        target.setStreetAddress(address.hasLine() ? joinStringTypes(address.getLine()) : null);
        return target;
    }

    private String joinStringTypes(List<StringType> list) {
        return list.stream()
                .map(StringType::getValueNotNull)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(" "));
    }
}
