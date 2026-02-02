package com.scnsoft.eldermark.shared.palatiumcare.resident;


import com.scnsoft.eldermark.shared.palatiumcare.location.PalCareLocationInDto;

public class PalCareResidentInDto {

    private Long palCareId;

    private String firstName;

    private String lastName;

    private PalCareLocationInDto palCareLocationInDto;

    public Long getPalCareId() {
        return palCareId;
    }

    public void setPalCareId(Long palCareId) {
        this.palCareId = palCareId;
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

    public PalCareLocationInDto getPalCareLocationInDto() {
        return palCareLocationInDto;
    }

    public void setPalCareLocationInDto(PalCareLocationInDto palCareLocationInDto) {
        this.palCareLocationInDto = palCareLocationInDto;
    }

    @Override
    public String toString() {
        return "PalCareResidentInDto{" +
                "palCareId=" + palCareId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", palCareLocationInDto=" + palCareLocationInDto +
                '}';
    }
}
