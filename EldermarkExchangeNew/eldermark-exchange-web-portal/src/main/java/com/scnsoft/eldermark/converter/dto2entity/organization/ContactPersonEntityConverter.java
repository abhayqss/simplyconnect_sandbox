package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.converter.PersonEntityConverter;
import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.ContactDto;
import com.scnsoft.eldermark.dto.NameDto;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.basic.Address;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.validation.ValidationRegExpConstants;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.EnumMap;
import java.util.regex.Pattern;

@Component
@Transactional(readOnly = true)
public class ContactPersonEntityConverter extends PersonEntityConverter implements Converter<ContactDto, Person> {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private Converter<Address, AddressDto> addressDtoConverter;

    @Autowired
    private Validator validator;

    @SuppressFBWarnings(value = "REDOS", justification = "Regexp simplified as much as possible")
    @Override
    public Person convert(ContactDto contactDto) {
        Person person;
        if (contactDto.getId() != null) {
            person = employeeService.getEmployeeById(contactDto.getId()).getPerson();
        } else {
            person = createNewPerson(contactDto.getOrganizationId());
        }

        var nameDto = new NameDto(contactDto.getFirstName(), contactDto.getLastName());

        var telecoms = new EnumMap<PersonTelecomCode, String>(PersonTelecomCode.class);
        telecoms.put(PersonTelecomCode.MC, contactDto.getMobilePhone());
        telecoms.put(PersonTelecomCode.WP, contactDto.getPhone());
        //taken from login since in new contacts login=email. Not updated in case of 4D contacts with login != email
        Pattern pattern = Pattern.compile(ValidationRegExpConstants.EMAIL_REGEXP);
        if (pattern.matcher(contactDto.getLogin()).matches()) {
            telecoms.put(PersonTelecomCode.EMAIL, contactDto.getLogin());
        }
        telecoms.put(PersonTelecomCode.FAX, contactDto.getFax());

        var address = contactDto.getIsCommunityAddressUsed() ? null : extractValidAddress(contactDto);

        update(person, address, nameDto, telecoms);
        return person;
    }

    private AddressDto extractValidAddress(ContactDto contact) {
        var address = contact.getAddress();
        if (address == null) {
            throw new ValidationException("address must be provided");
        }
        var result = validator.validate(address);
        if (CollectionUtils.isNotEmpty(result)) {
            throw new ConstraintViolationException(result);
        } else {
            return address;
        }
    }

    private AddressDto findCommunityAddress(Long communityId) {
        var community = communityService.get(communityId);
        return addressDtoConverter.convert(community.getAddresses().get(0));
    }
}
