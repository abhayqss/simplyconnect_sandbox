package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonAddress;
import com.scnsoft.eldermark.shared.carecoordination.AddressDto;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by pzhurba on 03-Nov-15.
 */

public interface AddressService {
    PersonAddress createPersonAddress(Database database, Person person, com.scnsoft.eldermark.schema.Address address);
    PersonAddress createPersonAddress(Database database, Person person, AddressDto address);
    void updatePersonAddress(PersonAddress personAddress, AddressDto address);
    AddressDto createAddressDto(PersonAddress personAddress);
}
