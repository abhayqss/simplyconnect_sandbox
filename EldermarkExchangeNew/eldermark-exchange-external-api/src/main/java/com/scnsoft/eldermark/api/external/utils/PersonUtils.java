package com.scnsoft.eldermark.api.external.utils;

import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import org.apache.commons.collections.CollectionUtils;

/**
 * Utility methods to work with personal telecoms and personal names.
 *
 */

public class PersonUtils {

    private PersonUtils() {
    }

    public static PersonTelecom createPersonTelecom(final Person person, final PersonTelecomCode code, final String value, String legacyTable) {
        return com.scnsoft.eldermark.util.PersonUtils.createPersonTelecom(person, code, value, legacyTable);
    }

    public static String getPersonTelecomValue(final Person person, final PersonTelecomCode code) {
        if (person == null || CollectionUtils.isEmpty(person.getTelecoms())) {
            return null;
        }
        for (PersonTelecom telecom : person.getTelecoms()) {
            if (code.toString().equals(telecom.getUseCode())) {
                return telecom.getValue();
            }
        }
        return null;
    }

    public static String getPersonEmailValue(final Person person) {
        return getPersonTelecomValue(person, PersonTelecomCode.EMAIL);
    }

    public static String getPersonPhoneValue(final Person person) {
        if (person == null || CollectionUtils.isEmpty(person.getTelecoms())) {
            return null;
        }
        for (PersonTelecom telecom : person.getTelecoms()) {
            if (!PersonTelecomCode.EMAIL.toString().equals(telecom.getUseCode())) {
                return telecom.getValue();
            }
        }
        return null;
    }

}
