package com.scnsoft.eldermark.dto;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class FullNameDto {

    private final String firstName;
    private final String lastName;
    private final String middleName;

    public FullNameDto(String firstName, String lastName, String middleName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var fullName = (FullNameDto) o;
        return ((StringUtils.equalsIgnoreCase(firstName, fullName.firstName) && StringUtils.equalsIgnoreCase(lastName, fullName.lastName)) ||
            (StringUtils.equalsIgnoreCase(firstName, fullName.lastName) && StringUtils.equalsIgnoreCase(lastName, fullName.firstName))) &&
            ((middleName == null && fullName.middleName != null) || (middleName != null && fullName.middleName == null) ||
                StringUtils.equalsIgnoreCase(middleName, fullName.middleName));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(StringUtils.lowerCase(firstName)) ^ Objects.hashCode(StringUtils.lowerCase(lastName));
    }
}
