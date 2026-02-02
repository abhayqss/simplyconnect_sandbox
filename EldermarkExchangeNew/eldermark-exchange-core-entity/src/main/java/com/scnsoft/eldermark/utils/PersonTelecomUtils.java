package com.scnsoft.eldermark.utils;

import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Optional;

public final class PersonTelecomUtils {

    private PersonTelecomUtils() {
    }

    public static Optional<PersonTelecom> find(Person person, PersonTelecomCode code) {
        return Optional.ofNullable(person).flatMap(p -> find(p.getTelecoms(), code));
    }

    public static Optional<PersonTelecom> find(Collection<PersonTelecom> personTelecoms, PersonTelecomCode code) {
        return CollectionUtils.emptyIfNull(personTelecoms).stream()
                .filter(tel -> code.toString().equals(tel.getUseCode()))
                .findFirst();
    }

    public static Optional<String> findValue(Person person, PersonTelecomCode code) {
        return Optional.ofNullable(person).flatMap(p -> findValue(p.getTelecoms(), code));
    }

    public static Optional<String> findValue(Collection<PersonTelecom> personTelecoms, PersonTelecomCode code) {
        return CollectionUtils.emptyIfNull(personTelecoms).stream()
                .filter(tel -> code.toString().equals(tel.getUseCode()))
                .findFirst()
                .map(PersonTelecom::getValue)
                .filter(StringUtils::isNotEmpty);
    }

    public static String findValue(Person person, PersonTelecomCode code, String defaultValue) {
        return findValue(person, code)
                .orElse(defaultValue);
    }

    public static String findValue(Collection<PersonTelecom> personTelecoms, PersonTelecomCode code, String defaultValue) {
        return findValue(personTelecoms, code)
                .orElse(defaultValue);
    }
}
