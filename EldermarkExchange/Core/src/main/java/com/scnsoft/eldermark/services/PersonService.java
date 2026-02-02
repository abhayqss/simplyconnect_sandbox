package com.scnsoft.eldermark.services;

import com.scnsoft.eldermark.dao.PersonTelecomDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.carecoordination.CareCoordinationConstants;
import com.scnsoft.eldermark.shared.carecoordination.utils.CareCoordinationUtils;
import com.scnsoft.eldermark.shared.phr.utils.Normalizer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Utility methods to work with personal telecoms and personal names.
 *
 * @author phomal
 * Created on 5/12/2017.
 */
@Service
public class PersonService {

    @Autowired
    private PersonTelecomDao personTelecomDao;

    public static PersonTelecom createPersonTelecom(final Person person, final PersonTelecomCode code, final String value, String legacyTable) {
        final PersonTelecom telecom = new PersonTelecom();
        telecom.setPerson(person);
        CareCoordinationConstants.setLegacyId(telecom);
        telecom.setLegacyTable(legacyTable);
        telecom.setSyncQualifier(code.getCode());

        telecom.setUseCode(code.name());
        telecom.setValue(value);
        if (PersonTelecomCode.EMAIL.equals(code)) {
            telecom.setValueNormalized(CareCoordinationUtils.normalizeEmail(value));
        } else {
            telecom.setValueNormalized(CareCoordinationUtils.normalizePhone(value));
        }
        telecom.setDatabase(person.getDatabase());
        telecom.setDatabaseId(person.getDatabaseId());
        return telecom;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void updateOrCreatePersonTelecom(Employee employee, PersonTelecomCode code, String value, String legacyTable) {
        final PersonTelecom telecom = getPersonTelecom(employee.getPerson(), code);
        if (telecom != null && StringUtils.isNotBlank(value)) {
            telecom.setValue(value);
        } else if (telecom != null && StringUtils.isBlank(value)) {
            employee.getPerson().getTelecoms().remove(telecom);
            personTelecomDao.delete(telecom);
        } else if (telecom == null && StringUtils.isNotBlank(value)) {
            employee.getPerson().getTelecoms().add(createPersonTelecom(employee.getPerson(), code, value, legacyTable));
        }
    }

    public static String getPersonTelecomNormalizedValue(final Person person, final PersonTelecomCode code) {
        final PersonTelecom personTelecom = getPersonTelecom(person, code);
        if (personTelecom != null) {
            return personTelecom.getValueNormalized();
        }
        return null;
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

    public static PersonTelecom getPersonTelecom(final Person person, final PersonTelecomCode code) {
        for (PersonTelecom telecom : person.getTelecoms()) {
            if (code.toString().equals(telecom.getUseCode())) {
                return telecom;
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

    public static Name createPersonName(Person person, String firstName, String lastName, String legacyTable) {
        final Name name = new Name();
        name.setPerson(person);
        CareCoordinationConstants.setLegacyId(name);
        name.setLegacyTable(legacyTable);
        name.setGiven(firstName);
        name.setGivenNormalized(CareCoordinationUtils.normalizeName(firstName));
        name.setFamily(lastName);
        name.setFamilyNormalized(CareCoordinationUtils.normalizeName(lastName));
        name.setNameUse("L");
        name.setDatabase(person.getDatabase());
        name.setDatabaseId(person.getDatabaseId());

        return name;
    }

    public static Name updateOrCreatePersonName(Person person, String firstName, String lastName, String legacyTable)  {
        Name name = null;
        List<Name> names = person.getNames();
        if (names!=null) {
            for (Name item : names) {
                if ("L".equals(item.getNameUse())) {
                    name = item;
                    break;
                }
            }
        }

        if (name == null) {
            return createPersonName(person, firstName, lastName, legacyTable);
        }
        else {
            name.setFamily(lastName);
            name.setFamilyNormalized(CareCoordinationUtils.normalizeName(lastName));

            name.setGiven(firstName);
            name.setGivenNormalized(CareCoordinationUtils.normalizeName(firstName));
            return name;
        }
    }

    public static boolean hasPhone(Person person, String valueNormalized) {
        return hasTelecom(person, valueNormalized, Arrays.asList("HP", "WP", "MC"));
    }

    public static boolean hasTelecom(Person person, String valueNormalized, List<String> useCodes) {
        for (PersonTelecom telecom : person.getTelecoms()) {
            // due to a bug `value_normalized` is not filled in for some rows in `PersonTelecom`
            if (useCodes.contains(telecom.getUseCode()) && ((telecom.getValueNormalized() == null && StringUtils.equals(Normalizer.normalizePhone(telecom.getValue()), valueNormalized)) || StringUtils.equals(telecom.getValueNormalized(), valueNormalized))) {
                return true;
            }
        }
        return false;
    }

}
