package com.scnsoft.eldermark.dto;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class NameDto {

    @NotEmpty
    @Size(max = 256)
    private String firstName;

    @NotEmpty
    @Size(max = 256)
    private String lastName;
    private String middleName;

    public NameDto() {
    }

    public NameDto(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getDisplayName() {
        return Stream.of(getFirstName(), getMiddleName(), getLastName()).filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(" "));
    }
}
