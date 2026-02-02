package com.scnsoft.eldermark.services.direct;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.PersonService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

public class RegistrationRequestDto {
    private Long id;
    private String secureEmail;
    private String fullName;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String phone;
    private String contactEmail;

    public Long getEmployeeId() {
        return id;
    }

    public void setEmployeeId(Long id) {
        this.id = id;
    }

    public String getSecureEmail() {
        return secureEmail;
    }

    public void setSecureEmail(String secureEmail) {
        this.secureEmail = secureEmail;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public boolean isValid() {
        return !(id == null || StringUtils.isBlank(secureEmail) ||
               StringUtils.isBlank(city) || StringUtils.isBlank(street) || StringUtils.isBlank(state) || StringUtils.isBlank(zip) ||
               StringUtils.isBlank(phone) || StringUtils.isBlank(contactEmail));
    }

    public static RegistrationRequestDto createFromEmployee(Employee employee) {
        RegistrationRequestDto dto = new RegistrationRequestDto();

        dto.setEmployeeId(employee.getId());
        dto.setSecureEmail(employee.getSecureMessaging());
        dto.setFullName(employee.getFullName());
        if (employee.getPerson() != null) {
            if (!CollectionUtils.isEmpty(employee.getPerson().getAddresses())) {
                final PersonAddress personAddress = employee.getPerson().getAddresses().get(0);
                dto.setCity(personAddress.getCity());
                dto.setStreet(personAddress.getStreetAddress());
                dto.setState(personAddress.getState());
                dto.setZip(personAddress.getPostalCode());
            }
            if (!CollectionUtils.isEmpty(employee.getPerson().getTelecoms())) {
                dto.setPhone(PersonService.getPersonTelecomValue(employee.getPerson(), PersonTelecomCode.WP));
                dto.setContactEmail(PersonService.getPersonTelecomValue(employee.getPerson(), PersonTelecomCode.EMAIL));
            }
        }

        return dto;
    }

}
