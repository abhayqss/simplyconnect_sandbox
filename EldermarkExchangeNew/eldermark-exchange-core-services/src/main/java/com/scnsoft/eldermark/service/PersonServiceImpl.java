package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.Name;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonAddress;
import com.scnsoft.eldermark.entity.PersonTelecom;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PersonServiceImpl implements PersonService {

    @Override
    public boolean updateLegacyId(Person person) {
        boolean updateNeeded = CareCoordinationConstants.updateLegacyId(person);

        if (CollectionUtils.isNotEmpty(person.getAddresses())) {
            for (PersonAddress personAddress : person.getAddresses()) {
                updateNeeded |= CareCoordinationConstants.updateLegacyIdFromParent(personAddress, person);
            }
        }
        if (CollectionUtils.isNotEmpty(person.getNames())) {
            for (Name name : person.getNames()) {
                updateNeeded |= CareCoordinationConstants.updateLegacyIdFromParent(name, person);
            }
        }
        if (CollectionUtils.isNotEmpty(person.getTelecoms())) {
            for (PersonTelecom telecom : person.getTelecoms()) {
                updateNeeded |= CareCoordinationConstants.updateLegacyIdFromParent(telecom, person);
            }
        }
        return updateNeeded;
    }
}
