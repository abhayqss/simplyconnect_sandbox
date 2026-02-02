package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonAddress;
import com.scnsoft.eldermark.entity.PersonAddressCode;
import com.scnsoft.eldermark.entity.State;
import com.scnsoft.eldermark.services.StateService;
import com.scnsoft.eldermark.services.carecoordination.CareCoordinationConstants;
import com.scnsoft.eldermark.web.entity.AddressEditDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author phomal
 * Created on 6/23/2017.
 */
@Service
public class AddressService {

    @Autowired
    StateService stateService;

    PersonAddress createAddressForPhrUser(AddressEditDto dto, Person person) {
        if (dto == null || !dto.hasContent()) {
            return null;
        }

        final PersonAddress address;
        final String legacyId = CareCoordinationConstants.SIMPLYCONNECT_PHR_LEGACY_ID_PREFIX + (person.getId() == null ? "" : person.getId());
        if (CollectionUtils.isEmpty(person.getAddresses())) {
            address = PersonAddress.Builder.aPersonAddress()
                    .withPostalCode(dto.getPostalCode())
                    .withStreetAddress(dto.getStreetAddress())
                    .withCity(dto.getCity())
                    .withCountry("US")
                    .withPostalAddressUse(PersonAddressCode.WP.name())
                    .withPerson(person)
                    .withDatabase(person.getDatabase())
                    .withLegacyTable(CareCoordinationConstants.SIMPLYCONNECT_PHR_ADDRESS_LEGACY_TABLE)
                    .withLegacyId(legacyId)
                    .build();
        } else {
            address = person.getAddresses().get(0);
            address.setCity(dto.getCity());
            address.setPostalCode(dto.getPostalCode());
            address.setStreetAddress(dto.getStreetAddress());
        }

        State state = stateService.findByAbbrOrFullName(dto.getState());
        if (state != null) {
            address.setState(state.getAbbr());
        }

        return address;
    }

    static PersonAddress cloneAddress(PersonAddress original) {
        if (original == null) {
            return null;
        }

        final String legacyId = CareCoordinationConstants.SIMPLYCONNECT_PHR_LEGACY_ID_PREFIX + (original.getPerson().getId() == null ? "" : original.getPerson().getId());
        final PersonAddress address = PersonAddress.Builder.aPersonAddress()
                    .withPostalCode(original.getPostalCode())
                    .withStreetAddress(original.getStreetAddress())
                    .withCity(original.getCity())
                    .withState(original.getState())
                    .withCountry(original.getCountry())
                    .withPostalAddressUse(original.getPostalAddressUse())
                    .withPerson(original.getPerson())
                    .withDatabase(original.getDatabase())
                    .withLegacyTable(CareCoordinationConstants.SIMPLYCONNECT_PHR_ADDRESS_LEGACY_TABLE)
                    .withLegacyId(legacyId)
                    .build();

        return address;
    }

}
