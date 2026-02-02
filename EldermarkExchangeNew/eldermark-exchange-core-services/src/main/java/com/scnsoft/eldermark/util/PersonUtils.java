package com.scnsoft.eldermark.util;

import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.service.CareCoordinationConstants;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;

import java.util.ArrayList;

public class PersonUtils {

    private PersonUtils() {
    }

    public static PersonTelecom updateOrCreateTelecom(Person person, PersonTelecomCode code, String value, String legacyTable) {
        return PersonTelecomUtils.find(person, code)
                .map(telecom -> {
                    telecom.setValue(value);
                    return telecom;
                })
                .orElseGet(() -> createPersonTelecom(person, code, value, legacyTable));
    }

    public static PersonTelecom createPersonTelecom(Person person, PersonTelecomCode code, String value, String legacyTable) {

        var telecom = new PersonTelecom();

        telecom.setPerson(person);

        telecom.setLegacyTable(legacyTable);
        CareCoordinationConstants.setLegacyId(telecom);

        telecom.setSyncQualifier(code.getCode());
        telecom.setUseCode(code.name());
        telecom.setValue(value);

        if (PersonTelecomCode.EMAIL.equals(code)) {
            telecom.setNormalized(CareCoordinationUtils.normalizeEmail(value));
        } else {
            telecom.setNormalized(CareCoordinationUtils.normalizePhone(value));
        }

        telecom.setOrganization(person.getOrganization());
        telecom.setOrganizationId(person.getOrganizationId());

        if (person.getTelecoms() == null) {
            person.setTelecoms(new ArrayList<>());
        }

        person.getTelecoms().add(telecom);

        return telecom;
    }
}
