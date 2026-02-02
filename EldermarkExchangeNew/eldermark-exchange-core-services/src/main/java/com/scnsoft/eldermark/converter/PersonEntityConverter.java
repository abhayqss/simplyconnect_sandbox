package com.scnsoft.eldermark.converter;

import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.NameDto;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonAddress;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.service.OrganizationService;
import com.scnsoft.eldermark.service.StateService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Map;

public abstract class PersonEntityConverter {

    @Autowired
    private StateService stateService;

    @Autowired
    private OrganizationService organizationService;


    protected void update(Person target, AddressDto addressDto, NameDto nameDto, Map<PersonTelecomCode, String> telecoms) {
        createOrUpdateFirstPersonAddress(target, addressDto);

        createOrUpdatePersonName(target, nameDto.getFirstName(), nameDto.getLastName());

        telecoms.forEach((code, value) ->
                createOrUpdatePersonTelecom(target, code, value));

    }

    protected Person createNewPerson(Long organizationId) {
        var organization = organizationService.findById(organizationId);
        return CareCoordinationUtils.createNewPerson(organization);
    }

    private void createOrUpdateFirstPersonAddress(Person person, AddressDto addressDto) {
        if (person.getAddresses() == null) {
            person.setAddresses(new ArrayList<>());
        }

        if (addressDto == null) {
            person.getAddresses().clear();
            return;
        }

        PersonAddress address = null;
        if (CollectionUtils.isNotEmpty(person.getAddresses())) {
            address = person.getAddresses().get(0);
        }
        if (address == null) {
            address = new PersonAddress();
            address.setPerson(person);
            com.scnsoft.eldermark.service.CareCoordinationConstants.setLegacyId(address);
            address.setLegacyTable(com.scnsoft.eldermark.service.CareCoordinationConstants.CCN_MANUAL_LEGACY_TABLE);

            address.setOrganization(person.getOrganization());
            address.setOrganizationId(person.getOrganizationId());

            address.setPostalAddressUse("HP");

            person.getAddresses().add(address);

        }

        address.setCity(addressDto.getCity());
        address.setPostalCode(addressDto.getZip());
        address.setStreetAddress(addressDto.getStreet());

        if (addressDto.getStateId() == null && StringUtils.isNotBlank(addressDto.getStateAbbr())) {
            address.setState(addressDto.getStateAbbr());
        } else {
            var state = stateService.findById(addressDto.getStateId()).orElseThrow();
            address.setState(state.getAbbr());
        }
    }

    private void createOrUpdatePersonName(Person person, String firstName, String lastName) {
        if (person.getNames() == null) {
            person.setNames(new ArrayList<>());
        }
        person.getNames().stream()
                .filter(item -> "L".equals(item.getNameUse()))
                .findFirst()
                .ifPresentOrElse(
                        name -> CareCoordinationUtils.fillName(name, firstName, lastName),
                        () -> CareCoordinationUtils.createAndAddName(person, firstName, lastName));
    }

    private void createOrUpdatePersonTelecom(final Person person, final PersonTelecomCode code,
                                             final String value) {
        if (person.getTelecoms() == null) {
            person.setTelecoms(new ArrayList<>());
        }

        PersonTelecom telecom = PersonTelecomUtils.find(person, code)
                .orElse(null);

        if (StringUtils.isEmpty(value)) {
            if (telecom != null) {
                person.getTelecoms().remove(telecom);
            }
            return;
        }

        if (telecom == null) {
            telecom = new PersonTelecom();
            telecom.setSyncQualifier(code.getCode());
            telecom.setUseCode(code.name());
            telecom.setOrganization(person.getOrganization());
            telecom.setOrganizationId(person.getOrganizationId());
            telecom.setLegacyTable(com.scnsoft.eldermark.service.CareCoordinationConstants.RBA_PERSON_TELECOM_LEGACY_TABLE);
            com.scnsoft.eldermark.service.CareCoordinationConstants.setLegacyId(telecom);

            telecom.setPerson(person);
            person.getTelecoms().add(telecom);
        }

        telecom.setValue(value);
        if (PersonTelecomCode.EMAIL.equals(code)) {
            telecom.setNormalized(CareCoordinationUtils.normalizeEmail(value));
        } else {
            telecom.setNormalized(CareCoordinationUtils.normalizePhone(value));
        }
    }

}
