package com.scnsoft.eldermark.consana.sync.client.services.converters;

import com.google.common.collect.ImmutableMap;
import com.scnsoft.eldermark.consana.sync.client.model.*;
import com.scnsoft.eldermark.consana.sync.client.model.entities.*;
import com.scnsoft.eldermark.consana.sync.client.utils.ConsanaUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.Patient;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional
public class ResidentToConsanaPatientDtoConverter implements Converter<Pair<Resident, Patient>, ConsanaPatientDto> {

    private static final Map<String, ContactPointUseMappingValue> contactPointUseMapping = ImmutableMap.of(
            "HP", new ContactPointUseMappingValue("phone", "home"),
            "WP", new ContactPointUseMappingValue("phone", "work"),
            "MC", new ContactPointUseMappingValue("phone", "mobile"),
            "EMAIL", new ContactPointUseMappingValue("email", null),
            "FAX", new ContactPointUseMappingValue("fax", null)
    );

    private static Map<String, String> addressUseMapping = ImmutableMap.of(
            "HOME", "home",
            "HP", "home",
            "WP", "WP"
    );

    private static final String PATIENT_ID = "http://xchangelabs.com/fhir/patient-id";

    private static final DateTimeFormatter BIRTH_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public ConsanaPatientDto convert(@NonNull Pair<Resident, Patient> source) {
        var resident = source.getFirst();
        var patient = source.getSecond();

        var patientDto = new ConsanaPatientDto();
        patientDto.setIdentifier(List.of(new ConsanaIdentifierDto(PATIENT_ID, patient.getIdElement().getIdPart())));
        patientDto.setActive(resident.getActive());
        patientDto.setGender(convertGender(resident.getGender()));
        patientDto.setBirthDate(convertLocalDate(resident.getBirthDate()));
        patientDto.setMaritalStatus(convertMaritalStatus(resident.getMaritalStatus()));
        var person = resident.getPerson();
        if (person == null) {
            return patientDto;
        }
        patientDto.setName(convertNames(resident));
        patientDto.setTelecom(convertTelecoms(person.getTelecoms()));
        patientDto.setAddress(convertAddresses(person.getAddresses()));
        return patientDto;
    }

    private String convertLocalDate(LocalDate localDate) {
        return Optional.ofNullable(localDate).map(BIRTH_DATE_FORMATTER::format).orElse(null);
    }

    private String convertGender(CcdCode ccdCode) {
        if (ccdCode == null) {
            return "unknown";
        }
        if ("M".equals(ccdCode.getCode())) {
            return "male";
        }
        if ("F".equals(ccdCode.getCode())) {
            return "female";
        }
        if ("UN".equals(ccdCode.getCode())) {
            return "other";
        }
        return "unknown";
    }

    private ConsanaCodeableConceptDto convertMaritalStatus(CcdCode ccdCode) {
        if (ccdCode == null) {
            return null;
        }
        var coding = new ConsanaCodingDto("http://hl7.org/fhir/ValueSet/marital-status", ccdCode.getCode(), ccdCode.getDisplayName());
        var target = new ConsanaCodeableConceptDto();
        target.setCoding(Collections.singletonList(coding));
        return target;
    }

    private List<ConsanaHumanNameDto> convertNames(Resident resident) {
        var names = resident.getPerson().getNames();
        if (CollectionUtils.isEmpty(names)) {
            return Collections.singletonList(new ConsanaHumanNameDto(
                    convertNameUse("L"),
                    ConsanaUtils.getFullName(resident.getFirstName(), resident.getLastName()),
                    Collections.singletonList(resident.getLastName()),
                    Collections.singletonList(resident.getFirstName())));
        }
        return names.stream()
                .filter(Objects::nonNull)
                .map(this::convertName)
                .collect(Collectors.toList());
    }

    private ConsanaHumanNameDto convertName(Name name) {
        var fullName = StringUtils.defaultString(name.getFullName(), ConsanaUtils.getFullName(name.getGiven(), name.getFamily()));
        return new ConsanaHumanNameDto(convertNameUse(name.getNameUse()), fullName,
                Collections.singletonList(name.getFamily()),
                Collections.singletonList(name.getGiven())
        );
    }

    private String convertNameUse(String nameUse) {
        if ("CL".equals(nameUse)) {
            return "usual";
        }
        if ("P".equals(nameUse)) {
            return "nickname";
        }
        if ("ASGN".equals(nameUse)) {
            return "temp";
        }
        return "official";
    }

    private List<ConsanaContactPointDto> convertTelecoms(List<PersonTelecom> telecoms) {
        return CollectionUtils.emptyIfNull(telecoms).stream()
                .filter(Objects::nonNull)
                .filter(telecom -> StringUtils.isNotEmpty(telecom.getValue()))
                .map(this::convertPersonTelecom)
                .collect(Collectors.toList());
    }

    private ConsanaContactPointDto convertPersonTelecom(PersonTelecom telecom) {
        var mapped = contactPointUseMapping.getOrDefault(telecom.getUseCode(), new ContactPointUseMappingValue(null, null));
        return new ConsanaContactPointDto(mapped.getSystem(), telecom.getValue(), mapped.getUse());
    }

    private List<ConsanaAddressDto> convertAddresses(List<PersonAddress> addresses) {
        return CollectionUtils.emptyIfNull(addresses).stream()
                .filter(Objects::nonNull)
                .map(this::convertPersonAddress)
                .collect(Collectors.toList());
    }

    private ConsanaAddressDto convertPersonAddress(PersonAddress personAddress) {
        return new ConsanaAddressDto(
                addressUseMapping.getOrDefault(personAddress.getPostalAddressUse(), null),
                Collections.singletonList(personAddress.getStreetAddress()),
                personAddress.getCity(),
                personAddress.getState(),
                personAddress.getPostalCode(),
                "USA"
        );
    }

    private static final class ContactPointUseMappingValue {
        private String system;
        private String use;

        public ContactPointUseMappingValue(String system, String use) {
            this.system = system;
            this.use = use;
        }

        public String getSystem() {
            return system;
        }

        public String getUse() {
            return use;
        }
    }
}
