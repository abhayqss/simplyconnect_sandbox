package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.beans.reports.model.EmergencyContact;
import com.scnsoft.eldermark.beans.reports.model.assessment.EmergencyContactsAware;
import com.scnsoft.eldermark.dto.client.EmergencyContactListItemDto;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EmergencyContactItemDtoConverter {

    public <T extends EmergencyContactsAware> List<EmergencyContactListItemDto> convert(T source) {
        var dtos = new ArrayList<EmergencyContactListItemDto>();
        if (source != null) {
            addDto(dtos, source.getEmergencyContact1());
            addDto(dtos, source.getEmergencyContact2());
            addDto(dtos, source.getEmergencyContact3());
        }
        return dtos;
    }

    private void addDto(List<EmergencyContactListItemDto> dtos, EmergencyContact contact) {
        if (isContactNotEmpty(contact)) {
            EmergencyContactListItemDto target = new EmergencyContactListItemDto();
            target.setFirstName(contact.getFirstName());
            target.setLastName(contact.getLastName());
            target.setFullName(getFullName(contact.getFirstName(), contact.getLastName()));
            target.setPhone(contact.getPhoneNumber());
            target.setAddress(getAddress(contact.getStreet(), contact.getCity(), contact.getState(), contact.getZipCode()));
            dtos.add(target);
        }
    }

    private boolean isContactNotEmpty(EmergencyContact c) {
        return c != null && !StringUtils.
                isAllEmpty(c.getFirstName(), c.getLastName(), c.getPhoneNumber(), c.getCity(), c.getState(), c.getStreet(), c.getZipCode());
    }

    private String getFullName(String firstName, String lastName) {
        return CareCoordinationUtils.concat(" ", firstName, lastName);
    }

    private String getAddress(String street, String city, String state, String zipCode) {
        return CareCoordinationUtils.concat(", ", street, city, state, zipCode);
    }
}