package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonAddress;
import com.scnsoft.eldermark.entity.State;
import com.scnsoft.eldermark.services.StateService;
import com.scnsoft.eldermark.shared.carecoordination.AddressDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.CareCoordinationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;

import static com.scnsoft.eldermark.services.carecoordination.CareCoordinationConstants.RBA_ADDRESS_LEGACY_TABLE;

/**
 * Created by pzhurba on 03-Nov-15.
 */
@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    StateService stateService;

    public PersonAddress createPersonAddress(Database database, Person person, com.scnsoft.eldermark.schema.Address address) {
        final PersonAddress personAddress = new PersonAddress();
        personAddress.setCity(address.getCity());
        personAddress.setStreetAddress(address.getStreet());

        State state = stateService.findByAbbrOrFullName(address.getState());
        if (state!=null) personAddress.setState(state.getAbbr());
        personAddress.setPostalCode(address.getZip());
        personAddress.setDatabase(database);
        personAddress.setLegacyTable(RBA_ADDRESS_LEGACY_TABLE);
        personAddress.setPostalAddressUse("HP");
        CareCoordinationConstants.setLegacyId(personAddress);
        personAddress.setPerson(person);
        return personAddress;
    }

    public PersonAddress createPersonAddress(Database database, Person person, AddressDto address) {
        final PersonAddress personAddress = new PersonAddress();
        personAddress.setPerson(person);
        personAddress.setLegacyTable(RBA_ADDRESS_LEGACY_TABLE);
        personAddress.setPostalAddressUse("HP");
        CareCoordinationConstants.setLegacyId(personAddress);
        personAddress.setDatabase(database);
        updatePersonAddress(personAddress, address);

        return personAddress;
    }

    public void updatePersonAddress(PersonAddress personAddress, AddressDto address) {
        if (address==null) return;
        State state = address.getState()==null ? null : stateService.get(address.getState().getId());
        if (state != null) {
            personAddress.setState(state.getAbbr());
        }
        personAddress.setStreetAddress(address.getStreet());
        personAddress.setCity(address.getCity());
        personAddress.setPostalCode(address.getZip());
    }

    public AddressDto createAddressDto(PersonAddress personAddress) {
           final AddressDto addressDto = new AddressDto();
           if (personAddress==null) return addressDto;
           if (!StringUtils.isEmpty(personAddress.getState())) {
               try {
                   final State state = stateService.findByAbbrOrFullName(personAddress.getState());
                   addressDto.setState(CareCoordinationUtils.createKeyValueDto(state));
               } catch (NoResultException e) {
                   // addressDto.setState(new KeyValueDto(-1L, personAddress.getState()));
               }
           }
           addressDto.setCity(personAddress.getCity());
           addressDto.setStreet(personAddress.getStreetAddress());
           addressDto.setZip(personAddress.getPostalCode());
           return addressDto;
       }

}
